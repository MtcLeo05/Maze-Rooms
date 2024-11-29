package com.leo.mazerooms.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    /**
     * Converts a Map to a Map that is surely mutable
     */
    public static <T, Y> Map<T, Y> mutable(Map<T, Y> map) {
        return new HashMap<>(map);
    }

}
