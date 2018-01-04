package com.devphill.music.model;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.Collator;

public class ModelUtil {

    /**
     * Checks Strings from ContentResolvers and replaces the default unknown value of
     * {@link MediaStore#UNKNOWN_STRING} with another String if needed
     * @param value The value returned from the ContentResolver
     * @param convertValue The value to replace unknown Strings with
     * @return A String with localized unknown values if needed, otherwise the original value
     */
    public static String parseUnknown(String value, String convertValue) {
        if (value == null || value.equals(MediaStore.UNKNOWN_STRING)) {
            return convertValue;
        } else {
            return value;
        }
    }

    public static int stringToInt(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long stringToLong(String string, long defaultValue) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int compareLong(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    public static int compareTitle(@Nullable String left, @Nullable String right) {
        return Collator.getInstance().compare(sortableTitle(left), sortableTitle(right));
    }

    /**
     * Creates a sortable String from a title, so that leading "the"s and "a"s can be removed. This
     * method will also strip the title's original case.
     * @param title The title to create a sortable String from
     * @return A new String with the same contents of {@code title}, but with any leading articles
     *         removed to conform to English standards.
     */
    @NonNull
    public static String sortableTitle(@Nullable String title) {
        if (title == null) {
            return "";
        }

        title = title.toLowerCase();

        if (title.startsWith("the ")) {
            return title.substring(4);
        } else if (title.startsWith("a ")) {
            return title.substring(2);
        } else {
            return title;
        }
    }

    public static int hashLong(long value) {
        return (int) (value ^ (value >>> 32));
    }
}
