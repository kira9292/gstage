package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.Notification;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Trouver toutes les notifications d'un utilisateur
    List<Notification> findByAppUserId(Long userId);

    // Trouver une notification par ID et utilisateur
    Optional<Notification> findByIdAndAppUserId(Long id, Long userId);

    // Trouver toutes les notifications non lues d'un utilisateur
    List<Notification> findByAppUserIdAndReadFalse(Long userId);

}
