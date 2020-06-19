package org.kdb.studio.db;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.ui.ConnectionsManagement;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Function;

public class AuthenticationDriverManager {

    private static final AuthenticationDriverManager INSTANCE = new AuthenticationDriverManager();

    public static AuthenticationDriverManager getInstance() {
        return INSTANCE;
    }

    private Set<AuthenticationDriver> authenticationDrivers;

    protected AuthenticationDriverManager() {
        authenticationDrivers = new HashSet<>();
    }

    public AuthenticationDriver[] getAuthenticationDrivers() {
        return authenticationDrivers.toArray(new AuthenticationDriver[] {});
    }

    public Optional<AuthenticationDriver> getAuthenticationDriverByName(String name) {
        return authenticationDrivers.stream().filter(d -> d.getName().equals(name)).findAny();
    }

    public void validateAuthenticationDriverByName(String name) {
        if (!StringUtil.isEmptyOrSpaces(name) && !name.equals(ConnectionsManagement.BASIC)) {
            if (!authenticationDrivers.stream().filter(d -> d.getName().equals(name)).findAny().isPresent()) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Authentication Driver not found", "Can't find authentication driver '" + name + "'. Fallback to default.",  NotificationType.WARNING));
            }


        }
    }

    public void addOrUpdate(AuthenticationDriver authenticationDriver) {
        authenticationDrivers.add(authenticationDriver);
    }

    public void remove(AuthenticationDriver authenticationDriver) {
        authenticationDrivers.remove(authenticationDriver);
    }
}
