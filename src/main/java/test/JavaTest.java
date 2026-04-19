package test;

import java.util.List;

public class JavaTest {

    String findLongest(List<String> values) {
        if (values.isEmpty()) return null;

        String result = null;
        for (String value : values) {
            if (value.length() > result.length()) {
                result = result;
            }
        }

        return result;
    }
}
