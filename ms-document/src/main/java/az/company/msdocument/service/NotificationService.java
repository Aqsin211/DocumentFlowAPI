package az.company.msdocument.service;

import az.company.msdocument.dao.entity.DocumentEntity;
import az.company.msdocument.exception.NotificationException;
import az.company.msdocument.client.UserClient;
import az.company.msdocument.model.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserClient userClient;

    @Value("${mail.from}")
    private String senderEmail;

    @Value("${mail.approvers}")
    private List<String> approverEmails;

    @Retryable(value = {NotificationException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void notifyApprovers(DocumentEntity documentEntity) {
        for (String email : approverEmails) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(email);
                helper.setFrom(senderEmail);
                helper.setSubject("Document Approval Request: " + documentEntity.getFilename());
                helper.setText("A new document has been submitted with ID: " + documentEntity.getFileId());
                mailSender.send(message);
            } catch (MessagingException e) {
                throw new NotificationException("Failed to send notification email to approvers");
            }
        }
    }

    @Retryable(value = {NotificationException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void notifySubmitter(DocumentEntity documentEntity, boolean approved) {
        try {
            UserResponse user = userClient.getUserById(documentEntity.getUploaderId().toString()).getBody();
            if (user == null || user.getGmail() == null) {
                throw new NotificationException("Submitter email not found for user ID " + documentEntity.getUploaderId());
            }

            String submitterEmail = user.getGmail();
            String status = approved ? "APPROVED" : "REJECTED";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(submitterEmail);
            helper.setFrom(senderEmail);
            helper.setSubject("Your Document was " + status);
            helper.setText("Your document with ID: " + documentEntity.getFileId() + " has been " + status + ".");
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new NotificationException("Failed to send notification email to submitter");
        }
    }
}
