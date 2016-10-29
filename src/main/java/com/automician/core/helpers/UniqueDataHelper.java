package com.automician.core.helpers;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class UniqueDataHelper {

    private static Map<String, String> dataContainer = new HashMap<String, String>();

    public static String the(String name) {
        if (!dataContainer.containsKey(name)) {
            dataContainer.put(name, name + currentTimeMillis());
        }
        return dataContainer.get(name);
    }

    public static void clearUniqueData() {
        dataContainer.clear();
    }

    public static void deleteUniqueData(String name) {
        dataContainer.remove(name);
    }
}
