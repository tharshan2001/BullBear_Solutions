package bullbear.app.controller;

import bullbear.app.entity.user.Notification;
import bullbear.app.entity.user.User;
import bullbear.app.security.CurrentUser;
import bullbear.app.utils.NotificationUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationUtil notificationUtil;

    public NotificationController(NotificationUtil notificationUtil) {
        this.notificationUtil = notificationUtil;
    }

    /**
     * Get all notifications for the currently logged-in user
     */
    @GetMapping
    public List<Notification> getUserNotifications(@CurrentUser User currentUser) {
        return notificationUtil.getUserNotifications(currentUser.getId().intValue());
    }
}