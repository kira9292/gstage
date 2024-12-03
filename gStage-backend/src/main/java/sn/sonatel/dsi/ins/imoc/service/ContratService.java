package sn.sonatel.dsi.ins.imoc.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

@Service
public class ContratService {

    private final ContratRepository contratRepository;
    private final CandidatRepository candidatRepository;

    public ContratService(ContratRepository contratRepository, CandidatRepository candidatRepository) {
        this.contratRepository = contratRepository;
        this.candidatRepository = candidatRepository;
    }

    @Transactional
    public ByteArrayResource genererContrat(ValidationStatuscandidat validation) {
        try (XWPFDocument document = new XWPFDocument()) {
            // Logo et en-tête
            XWPFParagraph headerParagraph = document.createParagraph();
            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setText("Sonatel");
            headerRun.setFontFamily("Arial");
            headerRun.setFontSize(16);
            headerRun.setColor("007F50"); // Couleur verte Sonatel

            // Titre du contrat
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("CONTRAT DE STAGE " + validation.getCandidat().getDemandeStage().getInternshipType());
            titleRun.setBold(true);
            titleRun.addBreak();
            titleRun.setText("N° REF. : " + validation.getCandidat().getDemandeStage().getReference());

            // Information société
            XWPFParagraph societyParagraph = document.createParagraph();
            XWPFRun societyRun = societyParagraph.createRun();
            societyRun.setText("ENTRE :");
            societyRun.addBreak();
            societyRun.setText("SONATEL, société anonyme avec Conseil d'Administration au capital social de 50 milliards de F CFA, immatriculée au RCCM de Dakar sous le numéro SN.DKR.74.B 61, ayant son siège social au 64, Voie de Dégagement Nord (VDN) BP 69 à Dakar (Sénégal), dûment représentée par Monsieur Sekou DRAME, agissant en qualité de Directeur Général de ladite société,");
            societyRun.addBreak();
            societyRun.addBreak();

            societyRun.setText("Ci-après dénommée « la SONATEL »");
            societyRun.addBreak();
            societyRun.setText("D'une part,");

            // Information stagiaire
            XWPFParagraph stagaireParagraph = document.createParagraph();
            XWPFRun stagiareRun = stagaireParagraph.createRun();
            stagiareRun.addBreak();
            stagiareRun.setText("ET");
            stagiareRun.addBreak();
            stagiareRun.setText("Prénom et Nom : " + validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName());
            stagiareRun.addBreak();
//            stagiareRun.setText("Sexe : " + validation.getCandidat().getGender());
//            stagiareRun.addBreak();
            stagiareRun.setText("Date et lieu de naissance : " + validation.getCandidat().getBirthDate() + " à " + validation.getCandidat().getBirthPlace());
            stagiareRun.addBreak();
            stagiareRun.setText("N°CIN : " + validation.getCandidat().getCni());
            stagiareRun.addBreak();
            stagiareRun.setText("Nationalité : " + validation.getCandidat().getNationality());
            stagiareRun.addBreak();
            stagiareRun.setText("Adresse : " + validation.getCandidat().getAddress());
            stagiareRun.addBreak();
            stagiareRun.setText("Formation en cours : "+ validation.getCandidat().getEducationLevel() +  " en " + validation.getCandidat().getFormation());
            stagiareRun.addBreak();
            stagiareRun.setText("Lieu d'affectation : "+ validation.getCandidat().getDemandeStage().getAppUser().getService().getName());
            stagiareRun.addBreak();
            stagiareRun.setText("Date debut et fin de Stage : "+ validation.getCandidat().getDemandeStage().getStartDate() + " au " + validation.getCandidat().getDemandeStage().getEndDate());
            stagiareRun.addBreak();
            stagiareRun.addBreak();

            stagiareRun.setText("Ci-après dénommée « le Stagiaire »");
            stagiareRun.addBreak();
            stagiareRun.setText("D'autre part,");
            stagiareRun.addBreak();

            ajouterArticle(document, "IL A ETE ARRETE ET CONVENU CE QUI SUIT:", "");
            // Articles du contrat
            ajouterArticle(document, "ARTICLE 1 : OBJET.",
                "1.1. Le présent contrat a pour objet de contribuer à la formation pédagogique de " +
                    validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName() +
                    ", (ci-après « stagiaire pédagogique ») qui accepte d'effectuer un Stage au sein de Sonatel.");

            ajouterArticle(document, "ARTICLE 3 : DUREE ET PRISE D'EFFET.",
                "Le présent contrat de Stage pédagogique est conclu pour une durée déterminée de 04 mois allant du " +
                    validation.getCandidat().getDemandeStage().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " au " + validation.getCandidat().getDemandeStage().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");

            ajouterArticle(document, "ARTICLE 4 : MONTANT DE L'ALLOCATION DE STAGE.",
                "Le Stagiaire percevra une allocation mensuelle de 60000 FCFA.\n\n" +
                    "Cette somme ne peut nullement être assimilée à une rémunération. Elle est laissée à la libre " +
                    "appréciation de l'entreprise d'accueil et est considérée comme une simple libéralité sans aucun " +
                    "caractère obligatoire.");

            // Signatures
            XWPFParagraph signatureParagraph = document.createParagraph();
            XWPFRun signatureRun = signatureParagraph.createRun();
            signatureRun.addBreak();
            signatureRun.setText("FAIT ET SIGNE A DAKAR LE");
            signatureRun.addBreak();
            signatureRun.setText("EN DEUX (2) EXEMPLAIRES DONT");
            signatureRun.addBreak();
            signatureRun.setText("UN (1) A ETE REMIS A CHAQUE PARTIE.");
            signatureRun.addBreak();
            signatureRun.addBreak();

            XWPFParagraph signatureLineParagraph = document.createParagraph();
            signatureLineParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun signatureLineRun = signatureLineParagraph.createRun();
            signatureLineRun.setText("POUR SONATEL                                                  POUR LE STAGIAIRE");

            // Conversion en ByteArrayResource
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            byte[] documentBytes = baos.toByteArray();

            Contrat contrat = new Contrat();
            contrat.setReference(generateUniqueReference());
            contrat.setStartDate(validation.getCandidat().getDemandeStage().getStartDate());
            contrat.setEndDate(validation.getCandidat().getDemandeStage().getEndDate());
            contrat.setCompensation(60000.);
            contrat.setStatus(ContractStatus.EN_SIGNATURE);
            contrat.setAssignmentSite(validation.getCandidat().getDemandeStage().getAppUser().getService().getName());
            contrat.setSignatureDate(LocalDate.now());
            contrat.setComments("Contrat généré");


            contrat.setDocs(documentBytes);
            Candidat c = this.candidatRepository.findByEmail(validation.getCandidat().getEmail());
            contrat.setCandidat(c);

            contratRepository.save(contrat);
            return new ByteArrayResource(documentBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'attestation", e);
        }
    }

    private String generateUniqueReference() {
        // Format: CONTRAT-STG-YYYY-MMDD-HHMMSS-XXXX
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MMdd-HHmmss"));

        String randomSuffix = String.format("%04d", new Random().nextInt(10000));
        return "CONTART-STG-" + timestamp + "-" + randomSuffix;
    }

    private void ajouterArticle(XWPFDocument document, String titre, String contenu) {
        XWPFParagraph articleParagraph = document.createParagraph();
        XWPFRun titleRun = articleParagraph.createRun();
        titleRun.setText(titre);
        titleRun.setBold(true);
        titleRun.addBreak();

        XWPFRun contentRun = articleParagraph.createRun();
        contentRun.setText(contenu);
        contentRun.addBreak();
    }
}
