package fr.mrcubee.util;

import com.mysql.jdbc.StringUtils;

import java.lang.reflect.Field;

public class Reflection {

    public static boolean setValue(Object instance, String fieldName, Object value) {
        Field field;

        if (instance == null || StringUtils.isEmptyOrWhitespaceOnly(fieldName))
            return false;
        try {
            field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        return false;
    }

}
