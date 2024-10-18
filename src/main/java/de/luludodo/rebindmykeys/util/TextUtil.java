package de.luludodo.rebindmykeys.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;
import net.minecraft.util.Language;

import java.util.*;

@SuppressWarnings("unused")
public class TextUtil {
    private static class NewLineVisitor implements StringVisitable.StyledVisitor<String> {
        private final Stack<StringVisitable> lines = new Stack<>();
        private StringVisitable current = StringVisitable.EMPTY;

        @Override
        public Optional<String> accept(Style style, String asString) {
            parse(asString, style);
            return Optional.empty();
        }

        public void parse(String string, Style style) {
            int i = string.indexOf('\n');
            if (i >= 0) {
                if (i > 0) {
                    append(string.substring(0, i), style);
                }
                newLine();
                if (i < string.length() - 1) {
                    parse(string.substring(i + 1), style);
                }
            } else {
                append(string, style);
            }
        }

        public void append(String string, Style style) {
            current = StringVisitable.concat(
                    current,
                    StringVisitable.styled(string, style)
            );
        }

        public void newLine() {
            lines.push(current);
            current = StringVisitable.EMPTY;
        }

        public Stack<StringVisitable> getResult() {
            if (current != null) {
                lines.push(current);
                current = null;
            }

            return lines;
        }
    }

    /**
     * Applies newlines to the text (a newline is indicated by {@code '\n'}).
     * @param text The text
     * @return A {@link List} where every new element is a new line of text
     * @see TextUtil#applyNewlinesAsText(Text)
     */
    public static List<StringVisitable> applyNewlines(Text text) {
        NewLineVisitor visitor = new NewLineVisitor();
        text.visit(visitor, Style.EMPTY);

        return visitor.getResult();
    }

    /**
     * Applies newlines to the text (a newline is indicated by {@code '\n'}).
     * @param text The text
     * @return A {@link List} where every new element is a new line of text
     * @see TextUtil#applyNewlines(Text)
     */
    public static List<OrderedText> applyNewlinesAsText(Text text) {
        return Language.getInstance().reorder(applyNewlines(text));
    }

    /**
     * Wraps a {@link List} of {@link StringVisitable} to the specified width.
     * This is done by wrapping each element of the {@link List} individually and then combining them.
     * So if the {@link List} is {@code ["foo", "bar"]} the result will never contain {@code "foobar"},
     * as it will keep both elements seperated.
     * @param textRenderer The textRenderer used to do the wrapping
     * @param lines The lines to be wrapped
     * @param width The target width
     * @return The wrapped text
     * @see TextRenderer#wrapLines(StringVisitable, int)
     */
    public static List<OrderedText> wrapLines(TextRenderer textRenderer, List<StringVisitable> lines, int width) {
        List<OrderedText> result = new ArrayList<>();
        for (StringVisitable line : lines) {
            result.addAll(textRenderer.wrapLines(line, width));
        }
        return result;
    }

    /**
     * Wraps lines with respect to newlines (a newline is indicated by {@code '\n'}).
     * @param textRenderer The textRenderer used to wrap lines to the specified width
     * @param text The text to be wrapped
     * @param width The target width
     * @return The wrapped text
     * @see TextUtil#applyNewlines(Text)
     * @see TextUtil#wrapLines(TextRenderer, List, int)
     * @see TextRenderer#wrapLines(StringVisitable, int)
     */
    public static List<OrderedText> wrapLinesWithNewlines(TextRenderer textRenderer, Text text, int width) {
        return wrapLines(textRenderer, applyNewlines(text), width);
    }
}
