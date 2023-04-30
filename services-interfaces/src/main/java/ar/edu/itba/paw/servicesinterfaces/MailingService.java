package ar.edu.itba.paw.servicesinterfaces;

import java.util.Map;

public interface MailingService {
    void sendEmail(String mailTo, String mailSubject, String template, Map<String, Object> templateVariables);
}
