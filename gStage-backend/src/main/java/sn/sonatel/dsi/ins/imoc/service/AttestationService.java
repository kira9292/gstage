package sn.sonatel.dsi.ins.imoc.service;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.dto.AttestationPDTO;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class AttestationService {
    private final AttestationPresenceRepository aPRepository;
    private final AttestationFinStageRepository aFinStage;
    private final CandidatRepository cRepository;

    public AttestationService(
        AttestationPresenceRepository aPRepository,
        AttestationFinStageRepository aFinStage,
        CandidatRepository cRepository
        ) {
        this.aPRepository = aPRepository;
        this.aFinStage = aFinStage;
        this.cRepository = cRepository;

    }

    public ByteArrayResource genererAttestation(ValidationStatuscandidat validation) {
        try (XWPFDocument document = new XWPFDocument()) {
            // En-tête
            XWPFParagraph headerParagraph = document.createParagraph();
            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setText("Direction des Systèmes d’Information");
            headerRun.addBreak();
            headerRun.setText("Département Ingénierie des Systèmes d’Information");
            headerRun.addBreak();
            headerRun.setText("Service " + validation.getCandidat().getDemandeStage().getAppUser().getService().getName());
            headerRun.setFontFamily("Arial");
            headerRun.setFontSize(11);
            headerRun.setBold(true);
            headerRun.addBreak();
            headerRun.addBreak();
            headerRun.addBreak();
            headerRun.addBreak();
            headerRun.addBreak();


            // Titre de l'attestation
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Attestation de Fin de stage");
            titleRun.setFontFamily("Arial");
            titleRun.setFontSize(16);
            titleRun.setBold(true);
            titleRun.setColor("000000");
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();

            // Corps du texte
            XWPFParagraph bodyParagraph = document.createParagraph();
            XWPFRun bodyRun = bodyParagraph.createRun();
            bodyRun.setFontFamily("Arial");
            bodyRun.setFontSize(12);

            bodyRun.setText("Je soussigné, M. " + validation.getCandidat().getDemandeStage().getAppUser().getFirstName() + " " + validation.getCandidat().getDemandeStage().getAppUser().getName() + ", Chef du Service "+ validation.getCandidat().getDemandeStage().getAppUser().getService().getName() +", atteste que :");
            bodyRun.addBreak();
            bodyRun.setText("M. " + validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName() +
                ", matricule " + validation.getCandidat().getDemandeStage().getReference() +
                ", a travaillé comme stagiaire dans la structure durant la période du " +
                validation.getCandidat().getDemandeStage().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " au " + validation.getCandidat().getDemandeStage().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.setText("En foi de quoi, cette présente attestation lui est délivrée pour servir et valoir ce que de droit.");
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.addBreak();
            // Signature
            XWPFParagraph signatureParagraph = document.createParagraph();
            signatureParagraph.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun signatureRun = signatureParagraph.createRun();
            signatureRun.setFontFamily("Arial");
            signatureRun.setFontSize(12);
            signatureRun.addBreak();
            signatureRun.setText("Fait à Dakar, le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            signatureRun.setBold(true);
            signatureRun.addBreak();
            signatureRun.addBreak();
            signatureRun.addBreak();
            signatureRun.setText("Service " + validation.getCandidat().getDemandeStage().getAppUser().getService().getName());
            signatureRun.addBreak();
            signatureRun.setText(validation.getCandidat().getDemandeStage().getAppUser().getFirstName() + " " + validation.getCandidat().getDemandeStage().getAppUser().getName());
            signatureRun.addBreak();



            // Conversion en ByteArrayResource
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            byte[] documentBytes = baos.toByteArray();

            AttestationFinStage aFS = new AttestationFinStage();
            aFS.setIssueDate(validation.getCandidat().getDemandeStage().getEndDate());
            aFS.setSignatureDate(LocalDate.now());
            aFS.setComments("Attestation de fin de stage générée");
            aFS.setReference(generateUniqueReference());

            aFS.setDocs(documentBytes);
            List<Candidat> candidats = this.cRepository.findAllByEmail(validation.getCandidat().getEmail());
            if (!candidats.isEmpty()) {
                Candidat c = candidats.get(candidats.size() - 1);
                // Traitez le dernier candidat
                System.out.println("Dernier candidat : " + c);
                c.setAttestationFinStage(aFS);
            }



            aFinStage.save(aFS);
            return new ByteArrayResource(documentBytes);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'attestation", e);
        }
    }

    private String generateUniqueReference() {
        // Format: SONATEL-STAGE-YYYY-MMDD-HHMMSS-XXXX
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MMdd-HHmmss"));

        String randomSuffix = String.format("%04d", new Random().nextInt(10000));
        return "SONATEL-STG-" + timestamp + "-" + randomSuffix;
    }

    public ByteArrayResource genererAttestationPresence(ValidationStatuscandidat validation, AttestationPDTO request) {
        try (XWPFDocument document = new XWPFDocument()) {
            // En-tête
            XWPFParagraph headerParagraph = document.createParagraph();
            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setText("Direction des Systèmes d’Information");
            headerRun.addBreak();
            headerRun.setText("Département " + validation.getCandidat().getDemandeStage().getAppUser().getService().getDepartemen().getName());
            headerRun.addBreak();
            headerRun.setText("Service " + validation.getCandidat().getDemandeStage().getAppUser().getService().getName());
            headerRun.setFontFamily("Arial");
            headerRun.setFontSize(11);
            headerRun.setBold(true);
            headerRun.addBreak();
            headerRun.addBreak();
            headerRun.addBreak();
            headerRun.addBreak();
            headerRun.addBreak();


            // Titre de l'attestation
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Attestation de Presence");
            titleRun.setFontFamily("Arial");
            titleRun.setFontSize(16);
            titleRun.setBold(true);
            titleRun.setColor("000000");
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();

            // Corps du texte
            XWPFParagraph bodyParagraph = document.createParagraph();
            XWPFRun bodyRun = bodyParagraph.createRun();
            bodyRun.setFontFamily("Arial");
            bodyRun.setFontSize(12);

            bodyRun.setText("Je soussigné, M. " + validation.getCandidat().getDemandeStage().getAppUser().getFirstName() + " " + validation.getCandidat().getDemandeStage().getAppUser().getName() + ", Chef du Service "+ validation.getCandidat().getDemandeStage().getAppUser().getService().getName() +", atteste que :");
            bodyRun.addBreak();
            bodyRun.setText("M. " + validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName() +
                ", matricule " + validation.getCandidat().getDemandeStage().getReference() +
                ", a travaillé comme stagiaire dans la structure durant la période du " +
                request.startDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " au " + request.endDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.setText("En foi de quoi, cette présente attestation lui est délivrée pour servir et valoir ce que de droit.");
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.addBreak();
            // Signature
            XWPFParagraph signatureParagraph = document.createParagraph();
            signatureParagraph.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun signatureRun = signatureParagraph.createRun();
            signatureRun.setFontFamily("Arial");
            signatureRun.setFontSize(12);
            signatureRun.addBreak();
            signatureRun.setText("Fait à Dakar, le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            signatureRun.setBold(true);
            signatureRun.addBreak();
            signatureRun.addBreak();
            signatureRun.addBreak();
            signatureRun.setText("Service " + validation.getCandidat().getDemandeStage().getAppUser().getService().getName());
            signatureRun.addBreak();
            signatureRun.setText(validation.getCandidat().getDemandeStage().getAppUser().getFirstName() + " " + validation.getCandidat().getDemandeStage().getAppUser().getName());
            signatureRun.addBreak();

            // Conversion en ByteArrayResource
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);



            AttestationPresence aP = new AttestationPresence();
            aP.setStartDate(request.startDate());
            aP.setEndDate(request.endDate());
            aP.setSignatureDate(LocalDate.now());
            aP.setStatus(true);
            aP.setComments("Attestation de présence générée");

//            String base64Document = Base64.getEncoder().encodeToString(documentBytes);
//            System.out.println("Base64 Document: " + base64Document);

            byte[] documentBytes = baos.toByteArray();


            aP.setDocs(documentBytes);
            List<Candidat> c = this.cRepository.findAllByEmail(request.email());
            if (!c.isEmpty()) {
                Candidat candidat = c.get(c.size() - 1);
                // Traitez le dernier candidat
                System.out.println("Dernier candidat : " + c);
                aP.setCandidat(candidat);
            }


            aPRepository.save(aP);
            return new ByteArrayResource(documentBytes);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'attestation", e);
        }
    }
}
