package sn.sonatel.dsi.ins.imoc.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A StagiairesProposer.
 */
@Entity
@Table(name = "stagiaires_proposer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StagiairesProposer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "demandeur")
    private String demandeur;

    @Column(name = "direction")
    private String direction;

    @Column(name = "nbre_stagiaire")
    private Integer nbreStagiaire;

    @Column(name = "profil_formation")
    private String profilFormation;

    @Column(name = "stagiaie_sous_recomandation")
    private String stagiaieSousRecomandation;

    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "motif")
    private String motif;

    @Column(name = "traitement")
    private String traitement;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StagiairesProposer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDemandeur() {
        return this.demandeur;
    }

    public StagiairesProposer demandeur(String demandeur) {
        this.setDemandeur(demandeur);
        return this;
    }

    public void setDemandeur(String demandeur) {
        this.demandeur = demandeur;
    }

    public String getDirection() {
        return this.direction;
    }

    public StagiairesProposer direction(String direction) {
        this.setDirection(direction);
        return this;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getNbreStagiaire() {
        return this.nbreStagiaire;
    }

    public StagiairesProposer nbreStagiaire(Integer nbreStagiaire) {
        this.setNbreStagiaire(nbreStagiaire);
        return this;
    }

    public void setNbreStagiaire(Integer nbreStagiaire) {
        this.nbreStagiaire = nbreStagiaire;
    }

    public String getProfilFormation() {
        return this.profilFormation;
    }

    public StagiairesProposer profilFormation(String profilFormation) {
        this.setProfilFormation(profilFormation);
        return this;
    }

    public void setProfilFormation(String profilFormation) {
        this.profilFormation = profilFormation;
    }

    public String getStagiaieSousRecomandation() {
        return this.stagiaieSousRecomandation;
    }

    public StagiairesProposer stagiaieSousRecomandation(String stagiaieSousRecomandation) {
        this.setStagiaieSousRecomandation(stagiaieSousRecomandation);
        return this;
    }

    public void setStagiaieSousRecomandation(String stagiaieSousRecomandation) {
        this.stagiaieSousRecomandation = stagiaieSousRecomandation;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public StagiairesProposer commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getMotif() {
        return this.motif;
    }

    public StagiairesProposer motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getTraitement() {
        return this.traitement;
    }

    public StagiairesProposer traitement(String traitement) {
        this.setTraitement(traitement);
        return this;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StagiairesProposer)) {
            return false;
        }
        return getId() != null && getId().equals(((StagiairesProposer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StagiairesProposer{" +
            "id=" + getId() +
            ", demandeur='" + getDemandeur() + "'" +
            ", direction='" + getDirection() + "'" +
            ", nbreStagiaire=" + getNbreStagiaire() +
            ", profilFormation='" + getProfilFormation() + "'" +
            ", stagiaieSousRecomandation='" + getStagiaieSousRecomandation() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            ", motif='" + getMotif() + "'" +
            ", traitement='" + getTraitement() + "'" +
            "}";
    }
}
