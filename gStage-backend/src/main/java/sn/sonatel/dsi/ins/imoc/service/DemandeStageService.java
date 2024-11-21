package sn.sonatel.dsi.ins.imoc.service;


import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.dto.DemandeStagecandidatDTO;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class  DemandeStageService {

    @Autowired
    private DemandeStageRepository demandeStageRepository;
    @Autowired
    private ValidationStatuscandidatRepository validationStatuscandidatRepository;
    @Autowired
    private ValidationCanditatService validationCanditatService;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private NotificationService notificationService;




    public void activation(Map<String, String> code) {
        ValidationStatuscandidat validation = this.validationCanditatService.getUserByCode(code.get("code"));
        if (Instant.now().isAfter(validation.getExpire())){
            Candidat c = candidatRepository.findByValidationStatuscandidatCode(code.get("code"));
//            this.validationStatuscandidatRepository.delete(validation);
//            this.demandeStageRepository.delete(c.getDemandeStage());
//            this.candidatRepository.delete(c);
            throw new RuntimeException("code expired");

        }else {
            validation.setActivation(Instant.now());
            this.validationStatuscandidatRepository.save(validation);

            Candidat c = candidatRepository.findByValidationStatuscandidatCode(code.get("code"));
            c.getDemandeStage().setStatus(InternshipStatus.EN_ATTENTE);

        }
    }

    public void resendcode(Map<String, String> mail) throws MessagingException, UnsupportedEncodingException {
        ValidationStatuscandidat validationStatusOptional = validationStatuscandidatRepository.findTopByCandidatEmailOrderByCreationDesc(mail.get("mail"));
        if (validationStatusOptional!=null) {
            this.notificationService.envoyercandidat(validationStatusOptional);

        } else {
            throw new RuntimeException("Aucun candidat trouvé avec cet email");
        }
    }

    public void accepterstagiaire(Map<String, String> mail) throws MessagingException, UnsupportedEncodingException {
        ValidationStatuscandidat validationStatusOptional = validationStatuscandidatRepository.findTopByCandidatEmailOrderByCreationDesc(mail.get("mail"));
        if (validationStatusOptional!=null) {
            this.notificationService.envoyerDocument(validationStatusOptional);

        } else {
            throw new RuntimeException("Aucun candidat trouvé avec cet email");
        }
    }

    public Optional<String> rejectInternship(Long id) {
        return demandeStageRepository
            .findById(id)
            .map(demandeStage -> {
                // Vérifier que la demande est en attente
                if (demandeStage.getStatus() != InternshipStatus.EN_ATTENTE) {
                    throw new BadRequestAlertException(
                        "La demande ne peut être rejetée que si elle est en attente",
                        "demandeStage",
                        "statusError"
                    );
                }

                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.REFUSE);

                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);
                return "Demande Stage " + id.toString() + " rejected";
            });
    }

    public Optional<String> archiveInternship(Long id) {
        return demandeStageRepository
            .findById(id)
            .map(demandeStage -> {
                // Vérifier que la demande est refusée
                if (demandeStage.getStatus() != InternshipStatus.REFUSE) {
                    throw new BadRequestAlertException(
                        "La demande ne peut être archivée que si elle est refusée",
                        "demandeStage",
                        "statusError"
                    );
                }

                // Mettre à jour le statut et la date d'archivage
                demandeStage.setStatus(InternshipStatus.ARCHIVE);

                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);
                return "Demande Stage " + id.toString() + " archived";
            });
    }

    public List<DemandeStage> findAllArchived() {
        return demandeStageRepository.findByStatus(InternshipStatus.ARCHIVE);
    }
}
