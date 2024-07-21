package de.luludodo.rebindmykeys.util;

import org.joml.Vector2i;

import java.util.Stack;

public class ScissorUtil {
    private static final Stack<Vector2i> scissorStack;
    static {
        scissorStack = new Stack<>();
        scissorStack.push(new Vector2i(0, 0));
    }

    public static void push() {
        scissorStack.push(new Vector2i(scissorStack.peek()));
    }

    public static void translate(int x, int y) {
        scissorStack.peek().add(x, y);
    }

    public static Vector2i peek() {
        return scissorStack.peek();
    }

    public static void pop() {
        scissorStack.pop();
    }
}
