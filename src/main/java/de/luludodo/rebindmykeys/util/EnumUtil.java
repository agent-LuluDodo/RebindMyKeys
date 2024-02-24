package de.luludodo.rebindmykeys.util;

import java.util.function.Consumer;

public class EnumUtil {
    public record MultiEnumCase<T extends Enum<?>>(Class<T> cl, Consumer<T> action) {
        public void action(Object o) {
            action.accept(cl.cast(o));
        }
    }

    public static void switchEnum(Object e, MultiEnumCase<?>... cases) {
        for (MultiEnumCase<?> enumCase : cases) {
            if (enumCase.cl().isInstance(e)) {
                enumCase.action(e);
            }
        }
    }

    public static <T extends Enum<?>> MultiEnumCase<T> switchCase(Class<T> cl, Consumer<T> action) {
        return new MultiEnumCase<>(cl, action);
    }
}
