package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.servicesinterfaces.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Override
    public Optional<NotificationType> getNotificationTypeById(String id) {
        return NotificationType.getByTypeName(id);
    }

    @Override
    public List<NotificationType> getNotifications() {
        return Arrays.stream(NotificationType.values()).collect(Collectors.toList());
    }
}
