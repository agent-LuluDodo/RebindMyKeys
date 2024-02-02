package de.luludodo.rebindmykeys.util;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * <b>Provides saving and loading methods for files.</b> <br>
 * These methods are designed to fail with <i>as little damage as possible</i>. For example, if a save fails during the writing
 * the file is reset to it's state before writing.
 */
public class FileUtil {
    public static final Logger LOG = LoggerFactory.getLogger("LuluDodo/FileUtils");

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

    @FunctionalInterface
    public interface Writer {
        void write(FileWriter writer) throws IOException;
    }
    @FunctionalInterface
    public interface Reader {
        void read(FileReader reader) throws IOException;
    }

    /**
     * Save a file to the path specified.
     * <p>
     *     <b>1</b> Created a backup <br>
     *     <b>2</b> Tries to write to file <br>
     *     <b>3.1</b> On success: Deletes backup <br>
     *     <b>3.2</b> On failure: Deletes failed attempt and replaces it with backup
     * </p>
     * @param path saves the file to this path
     * @param writer writes the files contents
     * @return whether the file was written successfully. If writing succeeds, but the backup deletion fails it would still return true.
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
                    LOG.error("Couldn't delete file writing to failed", e);
                    return false;
                }
            }
            if (backupPath.toFile().exists()) {
                try {
                    Files.move(backupPath, path);
                } catch (IOException e2) {
                    LOG.error("Couldn't move backup back to original place", e);
                    return false;
                }
            }
            return false;
        } catch (RuntimeException e) {
            LOG.error("Couldn't write file", e);
            if (FabricLoader.getInstance().isDevelopmentEnvironment())
                LOG.info("You should handle RuntimeExceptions in the Writer");
            return false;
        }
    }

    private static Path backupOf(Path path) {
        String pathString = path.toAbsolutePath().toString();
        String separator = path.getFileSystem().getSeparator();
        int lastSeparator = pathString.lastIndexOf(separator);
        return Path.of((lastSeparator != -1? pathString.substring(0, lastSeparator + separator.length()) : "") + "backup", pathString.substring(lastSeparator + separator.length()));
    }

    public static boolean load(Path path, Reader reader) {
        return load(path, reader, false);
    }
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
    public static boolean load(Path path, Reader reader, @NotNull Writer defaultsWriter) {
        return load(path, reader, () -> {
            LOG.info("Saving defaults to load");
            return save(path, defaultsWriter);
        });
    }
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
            if (FabricLoader.getInstance().isDevelopmentEnvironment())
                LOG.info("RuntimeExceptions should be handled in the Reader (wrap the Exception in InvalidFileContentException to show this)");
        }
        return true;
    }
}
