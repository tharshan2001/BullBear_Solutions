package bullbear.app.utils;

import bullbear.app.entity.user.Notification;
import bullbear.app.repository.user.NotificationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationUtil {

    private final NotificationRepository notificationRepository;

    public NotificationUtil(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Create and save a notification for a user
     *
     * @param userId  target user
     * @param type    type of notification (e.g., TRANSACTION, SYSTEM)
     * @param message message content
     */
    public void notifyUser(Long userId, String type, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    /**
     * Optional: Get all notifications for a user
     */
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Optional: Mark a notification as read
     */
    public void markAsRead(Integer notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }
}