package sn.sonatel.dsi.ins.imoc.service;


import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.dto.AttestationPDTO;
import sn.sonatel.dsi.ins.imoc.exceptions.InternshipRequestValidationException;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

@Service
public class ManagerService {

    @Autowired
    private DemandeStageRepository demandeStageRepository;

    private final NotificationService notificationService;
    private final ValidationStatuscandidatRepository validationStatuscandidatRepository;

    public ManagerService(NotificationService notificationService, ValidationStatuscandidatRepository validationStatuscandidatRepository) {
        this.notificationService = notificationService;
        this.validationStatuscandidatRepository = validationStatuscandidatRepository;
    }

    @Transactional
    public Optional<String> validateInternshipRequest(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation
                if (demandeStage.getStatus() != InternshipStatus.PROPOSE && demandeStage.getStatus() != InternshipStatus.EN_COURS) {
                    throw new InternshipRequestValidationException(
                        "La demande ne peut être traitée que si elle est en statut PROPOSE ou EN_COURS"
                    );
                }                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.ACCEPTE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " a été acceptée par le service " + demandeStage.getAppUser().getService().getName();
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
                validateRequestStatus(demandeStage, InternshipStatus.PROPOSE);
                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.REFUSE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " a été rejetée par le service " + demandeStage.getAppUser().getService().getName();
                notificationService.notifyAssistants(message, InternshipStatus.REFUSE);
                return "Demande Stage " + internshipId.toString() + " refused";
            });
    }

    @Transactional
    public Optional<String> confirmInternshipStart(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                // Vérifier que la demande est dans un état valide pour validation/rejet
                if (demandeStage.getStatus() != InternshipStatus.ACCEPTE && demandeStage.getStatus() != InternshipStatus.TERMINE) {
                    throw new InternshipRequestValidationException(
                        "La demande ne peut être mise EN_COURS que si elle est en statut ACCEPTE ou TERMINE "
                    );
                }                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.EN_COURS);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " est en cours dans le service " + demandeStage.getAppUser().getService().getName();
                notificationService.notifyAssistants(message, InternshipStatus.EN_COURS);
                return "Demande Stage " + internshipId.toString() + " started";
            });
    }

    @Transactional
    public Optional<String> markInternshipAsEnded(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                validateRequestStatus(demandeStage, InternshipStatus.EN_COURS);
                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.TERMINE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "Le stage " + demandeStage.getReference() + " est termine dans le service " + demandeStage.getAppUser().getService().getName();
                notificationService.notifyAssistants(message, InternshipStatus.TERMINE);
                return "Demande Stage " + internshipId.toString() + " ended";
            });
    }

    private void validateRequestStatus(DemandeStage demandeStage, InternshipStatus status) {
        // Vérifier que la demande est dans un état valide pour validation/rejet
        if (demandeStage.getStatus() != status) {
                throw new InternshipRequestValidationException(
                    "La demande ne peut être traitée que si elle est en statut PROPOSE"
                );
            }
    }

    @Transactional
    public void generateAttestation(Map<String, String> mail) throws MessagingException, UnsupportedEncodingException {
        ValidationStatuscandidat validationStatusOptional = validationStatuscandidatRepository.findTopByCandidatEmailOrderByCreationDesc(mail.get("mail"));
        if (validationStatusOptional!=null) {
            this.notificationService.envoyerAttestation(validationStatusOptional);
        } else {
            throw new RuntimeException("Aucun candidat trouvé avec cet email");
        }
    }
    @Transactional
    public void generateAttestationPresence(AttestationPDTO request) throws MessagingException, UnsupportedEncodingException {
        ValidationStatuscandidat validationStatusOptional = validationStatuscandidatRepository.findTopByCandidatEmailOrderByCreationDesc(request.email());

        if (validationStatusOptional!=null) {
            this.notificationService.envoyerAttestationPresence(validationStatusOptional,request);
        } else {
            throw new RuntimeException("Aucun candidat trouvé avec cet email");
        }
    }
}

