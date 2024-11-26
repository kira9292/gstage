package sn.sonatel.dsi.ins.imoc.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.exceptions.InternshipRequestValidationException;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;

import java.util.Optional;

@Service
public class ManagerService {

    @Autowired
    private DemandeStageRepository demandeStageRepository;

    private final NotificationService notificationService;

    public ManagerService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Transactional
    public Optional<String> validateInternshipRequest(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                validateRequestStatus(demandeStage);
                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.ACCEPTE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " a été acceptée.";
                notificationService.notifyAssistants(message, InternshipStatus.ACCEPTE);
                return "Demande Stage " + internshipId.toString() + " accepted";
            });
    }

    @Transactional
    public Optional<String> rejectInternshipRequest(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                validateRequestStatus(demandeStage);
                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.REFUSE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " a été rejetée.";
                notificationService.notifyAssistants(message, InternshipStatus.REFUSE);
                return "Demande Stage " + internshipId.toString() + " refused";
            });
    }

    private void validateRequestStatus(DemandeStage demandeStage) {
        // Vérifier que la demande est dans un état valide pour validation/rejet
        if (demandeStage.getStatus() != InternshipStatus.PROPOSE) {
            throw new InternshipRequestValidationException(
                "La demande ne peut être traitée que si elle est en statut PROPOSE"
            );
        }
    }

}

