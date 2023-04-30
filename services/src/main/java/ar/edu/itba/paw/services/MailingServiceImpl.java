package ar.edu.itba.paw.services;

import ar.edu.itba.paw.servicesinterfaces.MailingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

    private static final String FROM = "paw-2023a-04@hotmail.com";

    public void sendEmail(String mailTo, String mailSubject, String template, Map<String, Object> templateVariables) {
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
}
