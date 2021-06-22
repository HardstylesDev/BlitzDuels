package me.hardstyles.blitz.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@UtilityClass
public class ReflectionUtil {

    public void unregisterCommands(CommandMap map, String removing) {
        unregisterCommands(map, Collections.singleton(removing));
    }

    @SuppressWarnings("unchecked")
    public void unregisterCommands(CommandMap map, Collection<String> removing) {
        try {
            Field field = getField(map.getClass(), "knownCommands") != null ? getField(map.getClass(), "knownCommands")
                    : getField(map.getClass().getSuperclass(), "knownCommands");
            Map<String, Command> commands = (Map<String, Command>) field.get(map);

            for (String command : removing) {
                commands.remove(command);
            }

            field.set(map, commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}

