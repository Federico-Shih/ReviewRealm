package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.models.User;

import java.util.Map;

public interface MailingService {
    void sendChangePasswordEmail(ExpirationToken token, User user);
    void sendValidationTokenEmail(ExpirationToken token, User user);

    void sendEmail(String mailTo, String mailSubject, String template, Map<String, Object> templateVariables);
}
