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
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailingServiceImpl implements MailingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailingServiceImpl.class);
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private Environment env;
    @Autowired
    private MessageSource messageSource;
    private static final String FROM = "paw-2023a-04@hotmail.com";

    public void sendReviewDeletedEmail(Game game, User user) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("game", game.getName());
        templateVariables.put("gameId", game.getId());
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
        Object[] stringArgs = {};
        String subject = messageSource.getMessage("email.deletedreview.subject",
                stringArgs, LocaleContextHolder.getLocale());

        sendEmail(user.getEmail(), subject, "deletedreview", templateVariables);
    }

    @Override
    public void sendValidationTokenEmail(ExpirationToken token, User user) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
        templateVariables.put("token", token.getToken());
        templateVariables.put("user", user.getUsername());

        Object[] stringArgs = {};
        String subject = messageSource.getMessage("email.validation.subject",
                stringArgs, LocaleContextHolder.getLocale());

        sendEmail(user.getEmail(), subject, "validate", templateVariables);
    }

    @Override
    public void sendChangePasswordEmail(ExpirationToken token, User user) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
        templateVariables.put("token", token.getToken());
        templateVariables.put("user", user.getUsername());

        Object[] stringArgs = {};
        String subject = messageSource.getMessage("email.changepassword.subject",
                stringArgs, LocaleContextHolder.getLocale());

        sendEmail(user.getEmail(), subject, "changepassword", templateVariables);
    }

    private void sendEmail(String mailTo, String mailSubject, String template, Map<String, Object> templateVariables) {
        taskExecutor.execute(() -> {
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
            } catch(MessagingException | RuntimeException exception) {
                LOGGER.error("Error while sending email to {} with subject {}", mailTo, mailSubject, exception);
            }
        });
    }

    @Override
    public void sendReviewCreatedEmail(Review createdReview, User author, User follower) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("author", author.getUsername());
        templateVariables.put("game", createdReview.getReviewedGame().getName());
        templateVariables.put("reviewId", createdReview.getId());
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));

        Object[] stringArgs = {author.getUsername()};
        String subject = messageSource.getMessage("email.newreview.subject",
                stringArgs, LocaleContextHolder.getLocale());
        sendEmail(follower.getEmail(), subject, "newreview", templateVariables);
    }
}
