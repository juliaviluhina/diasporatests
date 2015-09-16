package core;

import static java.lang.System.currentTimeMillis;

public class Helpers {

    public static String newUniqueTagName() {
        return newUniqueTagName("tag");
    }

    public static String newUniqueTagName(String prefix) {
        return "#"+ prefix + currentTimeMillis();
    }

}
