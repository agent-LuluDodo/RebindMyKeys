package de.luludodo.rebindmykeys.util;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <b>Provides saving and loading methods for files.</b> <br>
 * These methods are designed to fail with <i>as little damage as possible</i>. For example, if a save fails during the writing
 * the file is reset to it's state before writing.
 */
public class FileUtil {
    /**
     * {@link Logger} for problems writing or reading files.
     */
    public static final Logger LOG = LoggerFactory.getLogger("LuluDodo/FileUtils");

    /**
     * Custom {@link RuntimeException} for Files which try to get parsed, but can't due to invalid contents.
     */
    public static class InvalidFileContentException extends RuntimeException {
        public InvalidFileContentException() {
            super();
        }

        public InvalidFileContentException(String message) {
            super(message);
        }

        public InvalidFileContentException(Exception cause) {
            super(cause);
        }

        public InvalidFileContentException(String message, Exception cause) {
            super(message, cause);
        }
    }

    /**
     * Interface for a {@link FileWriter}, which is allowed to throw {@link IOException IOExceptions}.
     */
    @FunctionalInterface
    public interface Writer {
        void write(FileWriter writer) throws IOException;
    }

    /**
     * Interface for a {@link FileReader}, which is allowed to throw {@link IOException IOExceptions}.
     */
    @FunctionalInterface
    public interface Reader {
        void read(FileReader reader) throws IOException;
    }

    /**
     * Save a file to the {@code path} specified.
     * <p>
     *     <b>1</b> Created a backup <br>
     *     <b>2</b> Tries to write to file <br>
     *     <b>3.1</b> On success: Deletes backup <br>
     *     <b>3.2</b> On failure: Deletes failed attempt and replaces it with backup
     * </p>
     * @param path saves the file to this {@code path}
     * @param writer writes the files contents
     * @return whether the file was written successfully. If writing succeeds, but the backup deletion fails it would still return {@code true}.
     */
    public static boolean save(Path path, Writer writer) {
        LOG.info("Saving " + path.getFileName());
        Path backupPath = backupOf(path);
        if (path.toFile().exists()) {
            try {
                Files.copy(path, backupPath);
            } catch (IOException e) {
                LOG.error("Couldn't create backup", e);
                return false;
            }
        } else {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                LOG.error("Couldn't create file", e);
                return false;
            }
        }
        try (FileWriter fileWriter = new FileWriter(path.toFile())) {
            writer.write(fileWriter);
            if (backupPath.toFile().exists()) {
                try {
                    Files.delete(backupPath);
                } catch (IOException e) {
                    LOG.error("Couldn't delete backup", e);
                    return true;
                }
            }
            return true;
        } catch (IOException e) {
            LOG.error("Couldn't write to file", e);
            if (path.toFile().exists()) {
                try {
                    Files.delete(path);
                } catch (IOException e2) {
                    LOG.error("Couldn't delete the file, where writing failed", e2);
                    return false;
                }
            }
            if (backupPath.toFile().exists()) {
                try {
                    Files.move(backupPath, path);
                } catch (IOException e2) {
                    LOG.error("Couldn't move backup back to original location", e2);
                    return false;
                }
            }
            return false;
        } catch (RuntimeException e) {
            LOG.error("Couldn't write file", e);
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) // We don't want to spam logs with this message, but we want devs to see it.
                LOG.warn("You should handle RuntimeExceptions in the Writer");
            return false;
        }
    }

    /**
     * Gets the backup {@link Path} version of the specified {@link Path}.
     * @param path The specified {@link Path}.
     * @return The backup version of the {@link Path}.
     */
    private static Path backupOf(Path path) {
        String pathString = path.toAbsolutePath().toString();
        String separator = path.getFileSystem().getSeparator();
        int lastSeparator = pathString.lastIndexOf(separator);
        return Path.of((lastSeparator != -1? pathString.substring(0, lastSeparator + separator.length()) : "") + "backup", pathString.substring(lastSeparator + separator.length())); // Basically some/path/file.txt -> some/path/backup/file.txt
    }

    /**
     * Loads the file located at the {@link Path} {@code path} and reads it using the {@link Reader} {@code reader}.
     * If the file isn't found a message will be logged at info level with the content "Couldn't find file to load".
     * @param path The {@link Path} of the file.
     * @param reader The {@link Reader} for the file.
     * @return If loading was successful. Will still return {@code true} if file couldn't be parsed.
     * @see FileUtil#load(Path, Reader, boolean)
     * @see FileUtil#load(Path, Reader, Writer)
     * @see FileUtil#load(Path, Reader, Supplier)
     */
    public static boolean load(Path path, Reader reader) {
        return load(path, reader, false);
    }

    /**
     * Loads the file located at the {@link Path} {@code path} and reads it using the {@link Reader} {@code reader}.
     * If the file isn't found a message will be logged at
     * (info if {@code requireFile} equals {@code false} and error if it equals {@code true})
     * level with the content "Couldn't find file to load".
     * @param path The {@link Path} of the file.
     * @param reader The {@link Reader} for the file.
     * @param requireFile If the file not found message should be logged at info or error level.
     * @return If loading was successful. Will still return {@code true} if file couldn't be parsed.
     * @see FileUtil#load(Path, Reader)
     * @see FileUtil#load(Path, Reader, Writer)
     * @see FileUtil#load(Path, Reader, Supplier)
     */
    public static boolean load(Path path, Reader reader, boolean requireFile) {
        return load(path, reader, () -> {
            if (requireFile) {
                LOG.error("Couldn't find file to load");
            } else {
                LOG.info("Couldn't find file to load");
            }
            return false;
        });
    }

    /**
     * Loads the file located at the {@link Path} {@code path} and reads it using the {@link Reader} {@code reader}.
     * If the file isn't found the {@code defaultsWriter} will try to write the defaults.
     * If it fails the loading will be canceled. But if it succeeds loading will continue.
     * @param path The {@link Path} of the file.
     * @param reader The {@link Reader} for the file.
     * @param defaultsWriter Writer to write the default file if no file is found.
     * @return If loading was successful. Will still return {@code true} if file couldn't be parsed.
     * @see FileUtil#load(Path, Reader)
     * @see FileUtil#load(Path, Reader, boolean)
     * @see FileUtil#load(Path, Reader, Supplier)
     */
    public static boolean load(Path path, Reader reader, @NotNull Writer defaultsWriter) {
        return load(path, reader, () -> {
            LOG.info("Saving defaults to load");
            return save(path, defaultsWriter);
        });
    }

    /**
     * Loads the file located at the {@link Path} {@code path} and reads it using the {@link Reader} {@code reader}.
     * If the file isn't found the {@code noFileHandler} will be run.
     * If it returns {@code false} the loading will be canceled. But if it returns {@code true} loading will continue.
     * @param path The {@link Path} of the file.
     * @param reader The {@link Reader} for the file.
     * @param noFileHandler Handler if no file is found. If the handler returns {@code true} loading will continue.
     * @return If loading was successful. Will still return {@code true} if file couldn't be parsed.
     * @see FileUtil#load(Path, Reader)
     * @see FileUtil#load(Path, Reader, boolean)
     * @see FileUtil#load(Path, Reader, Writer)
     */
    public static boolean load(Path path, Reader reader, Supplier<Boolean> noFileHandler) {
        LOG.info("Loading " + path.getFileName());
        if (!path.toFile().exists()) {
            if (!noFileHandler.get())
                return false;
        }
        try (FileReader fileReader = new FileReader(path.toFile())) {
            reader.read(fileReader);
        } catch (IOException e) {
            LOG.error("Couldn't read file", e);
            return false;
        } catch (InvalidFileContentException e) {
            LOG.error("Couldn't parse file", e);
        } catch (RuntimeException e) {
            LOG.error("Couldn't parse file", e);
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) // We don't want to spam logs with this message, but we want devs to see it.
                LOG.warn("RuntimeExceptions should be handled in the Reader (wrap the Exception in InvalidFileContentException to show this)");
        }
        return true;
    }

    public static String toValidFilename(String filename) {
        return filename.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }
}