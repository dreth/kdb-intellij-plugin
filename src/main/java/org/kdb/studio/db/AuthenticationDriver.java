package org.kdb.studio.db;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.lang.UrlClassLoader;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AuthenticationDriver {

    private static Pattern pattern = Pattern.compile("(.*?):(.*?)@(.*?):(.*?)");

    private String name;

    private String clazz;

    private Set<String> jarFiles;

    public AuthenticationDriver() {
        this.jarFiles = new HashSet<>();
    }

    public AuthenticationDriver(String name, String clazz, Collection<String> jarFiles) {
        this.name = name;
        this.clazz = clazz;
        this.jarFiles = new HashSet<>(jarFiles);
    }

    public String getName() {
        return name;
    }

    public String getClazz() {
        return clazz;
    }

    public Set<String> getJarFiles() {
        return jarFiles;
    }

    public Function<String, String> createAuthenticationFunction() {
        if (!StringUtil.isEmptyOrSpaces(clazz)) {
            try {
                Class<?> function = urlClassLoader().loadClass(clazz);
                return Optional.of(function.getDeclaredConstructor().newInstance()).filter(Function.class::isInstance).map(Function.class::cast).get();
            } catch (Exception e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Failed to instantiate authentication function", e.toString(), NotificationType.WARNING));
            }
        }
        return null;

    }

    public static Function<String, String> createBasicAuthenticationFunction() {
        return (s) -> {
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                return matcher.group(1) + ":" + matcher.group(2);
            }
            throw new RuntimeException(String.format("Unexpected format [%s]", s));
        };
    }

    private UrlClassLoader urlClassLoader() {
        return UrlClassLoader.build().useCache().files(jarFiles.stream().map(Paths::get).collect(Collectors.toList())).get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationDriver that = (AuthenticationDriver) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
