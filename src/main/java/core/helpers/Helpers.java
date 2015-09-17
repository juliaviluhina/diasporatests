package core.helpers;

import java.util.List;

public class Helpers {

    public static String[] listToArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

}
