package sn.sonatel.dsi.ins.imoc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.sonatel.dsi.ins.imoc.domain.Notification;
import sn.sonatel.dsi.ins.imoc.repository.NotificationRepository;
import sn.sonatel.dsi.ins.imoc.service.NotificationService;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    public NotificationController(NotificationRepository notificationRepository, NotificationService notificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }
    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsForUser(@PathVariable Long userId) {
        return notificationRepository.findByAppUserId(userId);
    }
    // Marquer une notification comme lue
    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<Void> markAsRead(
        @PathVariable Long id,
        @RequestParam Long userId // ID de l'utilisateur connecté
    ) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    // Supprimer une notification
    @DeleteMapping("/{id}/delete-notification")
    public ResponseEntity<Void> deleteNotification(
        @PathVariable Long id,
        @RequestParam Long userId
    ) {
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok().build();
    }

    // Marquer toutes les notifications comme lues
    @PutMapping("/mark-all-as-read")
    public ResponseEntity<Void> markAllAsRead(
        @RequestParam Long userId // ID de l'utilisateur connecté
    ) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    //Supprimer toutes les notifications
    @DeleteMapping("/delete-all-notifications")
    public ResponseEntity<Void> deleteAllNotification(
        @RequestParam Long userId
    ) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
}
