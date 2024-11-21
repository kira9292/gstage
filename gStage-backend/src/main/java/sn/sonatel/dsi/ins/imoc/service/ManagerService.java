package sn.sonatel.dsi.ins.imoc.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.dto.ValidationRequest;
import sn.sonatel.dsi.ins.imoc.exceptions.InternshipRequestValidationException;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;

@Service
public class ManagerService {

    @Autowired
    private DemandeStageRepository demandeStageRepository;

    @Transactional
    public ResponseEntity<?> validateInternshipRequest(Long requestId, ValidationRequest validationRequest) {
        return demandeStageRepository.findById(requestId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                validateRequestStatus(demandeStage);
                // Mettre à jour le statut
                updateInternshipRequestStatus(demandeStage, validationRequest);

                // Sauvegarder la demande
                DemandeStage savedDemandeStage = demandeStageRepository.save(demandeStage);

                return ResponseEntity.ok(savedDemandeStage);
            })
            .orElseThrow(() -> new InternshipRequestValidationException("Demande de stage introuvable"));
    }

    private void validateRequestStatus(DemandeStage demandeStage) {
        // Vérifier que la demande est dans un état valide pour validation/rejet
        if (demandeStage.getStatus() != InternshipStatus.PROPOSE) {
            throw new InternshipRequestValidationException(
                "La demande ne peut être traitée que si elle est en statut PROPOSE"
            );
        }
    }

    private void updateInternshipRequestStatus(DemandeStage demandeStage, ValidationRequest validationRequest) {
        if (Boolean.TRUE.equals(validationRequest.getValidated())) {
            demandeStage.setStatus(InternshipStatus.ACCEPTE);
        } else {
            demandeStage.setStatus(InternshipStatus.REFUSE);
        }
    }
}

