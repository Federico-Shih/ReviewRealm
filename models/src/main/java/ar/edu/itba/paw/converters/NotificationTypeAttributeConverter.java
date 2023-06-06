package ar.edu.itba.paw.converters;

import ar.edu.itba.paw.enums.NotificationType;

import javax.persistence.AttributeConverter;

public class NotificationTypeAttributeConverter implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType notificationType) {
        return notificationType != null ? notificationType.getTypeName() : null;
    }

    @Override
    public NotificationType convertToEntityAttribute(String s) {
        return s != null ? NotificationType.valueFrom(s) : null;
    }
}
