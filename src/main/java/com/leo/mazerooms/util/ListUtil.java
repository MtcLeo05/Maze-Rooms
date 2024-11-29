package com.leo.mazerooms.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    /**
     * Creates a MUTABLE list of type T
     */
    @SafeVarargs
    public static <T> List<T> of(T... ts) {
        return new ArrayList<>(
            List.of(ts)
        );
    }

    /**
     * Converts a List to a List that is surely mutable
     */
    public static <T> List<T> mutable(List<T> list) {
        return new ArrayList<>(list);
    }

}
