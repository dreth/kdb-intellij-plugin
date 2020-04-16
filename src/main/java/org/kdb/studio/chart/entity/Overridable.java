package org.kdb.studio.chart.entity;

import java.lang.reflect.Field;
import java.util.*;

public interface Overridable<T> {

    void override(T obj);

    Set<Class> primitiveNumbers = new HashSet<>(Arrays.asList(byte.class, short.class, int.class, long.class, double.class, float.class));

    static void overrideObject(Object origin, Object overriden) {
        for (Field field : origin.getClass().getDeclaredFields()) {
            try {
                Object newValue = field.get(overriden);
                if (newValue != null) {
                    if (Overridable.class.isAssignableFrom(field.getType())) {
                        Object value = field.get(origin);
                        if (value != null) {
                            Overridable.class.cast(value).override(newValue);
                        } else {
                            //field.set(origin, newValue);
                        }
                    } else if (Collection.class.isAssignableFrom(field.getType())) {
                        System.out.println("Ignoring " + field.getName() + " " + field.getType().getSimpleName());
                    } else {
                        if (!primitiveNumbers.contains(field.getType()) || Number.class.cast(newValue).floatValue() > 0) {
                            field.set(origin, newValue);
                        }
                    }
                }
            } catch (IllegalAccessException e) {

            }

        }
    }

}
