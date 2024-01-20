package de.luludodo.rebindmykeys.util;

import java.util.Collection;

public class CollectionUtil {
    public static boolean shareOneOrMoreElements(Collection<?> c1, Collection<?> c2) {
        for (Object o : c1) {
            if (c2.contains(o))
                return true;
        }
        return false;
    }
}
