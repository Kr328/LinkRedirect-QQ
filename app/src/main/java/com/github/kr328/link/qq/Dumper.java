package com.github.kr328.link.qq;

import android.content.Intent;
import android.os.Bundle;

import java.util.Collection;
import java.util.Map;

class Dumper {
    static String dump(String callingPackage, Intent intent) {
        return "Intent from " + callingPackage + "\n" +
                "  Action: " + intent.getAction() + '\n' +
                "  Category: " + intent.getCategories() + '\n' +
                "  Data: " + intent.getData() + '\n' +
                "  Package: " + intent.getPackage() + "\n" +
                "  Component: " + intent.getComponent() + "\n" +
                "  Extra: " + dump(intent.getExtras(), "    ");
    }

    private static String dump(Bundle bundle, String padding) {
        if (bundle == null)
            return "null";

        StringBuilder sb = new StringBuilder();

        for (String key : bundle.keySet()) {
            Object object = bundle.get(key);

            sb.append('\n').append(padding).append(key).append(": ");

            if (object == null)
                sb.append("null").append('\n');
            else if (object instanceof Bundle)
                sb.append(dump((Bundle) object, padding + "  "));
            else if (object instanceof Collection)
                sb.append(dump((Collection<?>) object, padding + "  "));
            else if (object instanceof Map)
                sb.append(dump((Map<?, ?>) object, padding + "  "));
            else
                sb.append(object.toString());
        }

        return sb.toString();
    }

    private static String dump(Collection<?> collection, String padding) {
        if (collection == null)
            return "null";

        StringBuilder sb = new StringBuilder();

        for (Object object : collection) {
            sb.append('\n').append(padding).append("- ");

            if (object == null)
                sb.append("null").append('\n');
            else if (object instanceof Bundle)
                sb.append(dump((Bundle) object, padding + "  "));
            else if (object instanceof Collection)
                sb.append(dump((Collection<?>) object, padding + "  "));
            else if (object instanceof Map)
                sb.append(dump((Map<?, ?>) object, padding + "  "));
            else
                sb.append(object.toString());
        }

        return sb.toString();
    }

    private static String dump(Map<?, ?> map, String padding) {
        if (map == null)
            return "null";

        StringBuilder sb = new StringBuilder();

        for (Object key : map.keySet()) {
            Object object = map.get(key);

            sb.append('\n').append(padding).append(key).append(": ");

            if (object == null)
                sb.append("null").append('\n');
            else if (object instanceof Bundle)
                sb.append(dump((Bundle) object, padding + "  "));
            else if (object instanceof Collection)
                sb.append(dump((Collection<?>) object, padding + "  "));
            else if (object instanceof Map)
                sb.append(dump((Map<?, ?>) object, padding + "  "));
            else
                sb.append(object.toString());
        }

        return sb.toString();
    }
}