package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.enums.NotificationType;

import java.util.*;

public class NotificationsForm {
    private List<String> notificationSettings;

    public List<String> getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(List<String> notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public Map<NotificationType, Boolean> getConvertedNotificationSettings() {
        Map<NotificationType, Boolean> map = new HashMap<>();
        if(notificationSettings != null) {
            for (String notificationChecked : notificationSettings) {
                Optional<NotificationType> notificationType = NotificationType.getByTypeName(notificationChecked);
                notificationType.ifPresent(type -> map.put(type, true));
            }
        }
        for(NotificationType type : NotificationType.values()) {
            map.putIfAbsent(type, false);
        }
        return map;
    }
}
