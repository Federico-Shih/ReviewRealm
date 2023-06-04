package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class MailingServiceImpl implements MailingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailingServiceImpl.class);
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private Environment env;
    @Autowired
    private MessageSource messageSource;
    private static final String FROM = "paw-2023a-04@hotmail.com";

    @Async
    public void sendReviewDeletedEmail(Game game, User user) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("game", game.getName());
        templateVariables.put("gameId", game.getId());
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
        Object[] stringArgs = {};
        String subject = messageSource.getMessage("email.deletedreview.subject",
                stringArgs, user.getLanguage());
        LOGGER.info("Sending review deleted to {}", user.getEmail());
        sendEmail(user.getEmail(), subject, "deletedreview", templateVariables, user.getLanguage());
    }

    @Async
    @Override
    public void sendValidationTokenEmail(ExpirationToken token, User user) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
        templateVariables.put("token", token.getToken());
        templateVariables.put("user", user.getUsername());

        Object[] stringArgs = {};
        String subject = messageSource.getMessage("email.validation.subject",
                stringArgs, user.getLanguage());
        LOGGER.info("Sending validation token to {}", user.getEmail());
        sendEmail(user.getEmail(), subject, "validate", templateVariables, user.getLanguage());
    }

    @Async
    @Override
    public void sendChangePasswordEmail(ExpirationToken token, User user) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
        templateVariables.put("token", token.getToken());
        templateVariables.put("user", user.getUsername());

        Object[] stringArgs = {};
        String subject = messageSource.getMessage("email.changepassword.subject",
                stringArgs, user.getLanguage());
        LOGGER.info("Sending change password token to {}", user.getEmail());
        sendEmail(user.getEmail(), subject, "changepassword", templateVariables, user.getLanguage());
    }

    @Async
    @Override
    public void sendReviewCreatedEmail(Review createdReview, User author, User follower) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("author", author.getUsername());
        templateVariables.put("game", createdReview.getReviewedGame().getName());
        templateVariables.put("reviewId", createdReview.getId());
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));

        Object[] stringArgs = {author.getUsername()};
        String subject = messageSource.getMessage("email.newreview.subject",
                stringArgs, follower.getLanguage());
        LOGGER.info("Sending new review created to {} for review id {}", follower.getEmail(), createdReview.getId());
        sendEmail(follower.getEmail(), subject, "newreview", templateVariables, follower.getLanguage());
    }

    private void sendEmail(String mailTo, String mailSubject, String template, Map<String, Object> templateVariables, Locale userLocale) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setFrom(FROM);
            mimeMessageHelper.setSubject(mailSubject);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(templateVariables);
            String htmlBody = templateEngine.process(template, thymeleafContext);
            mimeMessageHelper.setText(htmlBody, true);

            javaMailSender.send(mimeMessage);
            LOGGER.info("Email sent to {} with subject {}", mailTo, mailSubject);
        } catch (MessagingException | RuntimeException exception) {
            LOGGER.error("Error while sending email to {} with subject {}", mailTo, mailSubject, exception);
        }
    }
}
