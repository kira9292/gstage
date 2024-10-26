package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RestaurationStagiaire.
 */
@Table("restauration_stagiaire")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurationStagiaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    @Column("end_date")
    private LocalDate endDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private Boolean status;

    @Column("card_number")
    private String cardNumber;

    @Transient
    @JsonIgnoreProperties(value = { "contrats", "demandeStage", "manager" }, allowSetters = true)
    private Candidat candidat;

    @Column("candidat_id")
    private Long candidatId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RestaurationStagiaire id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public RestaurationStagiaire startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public RestaurationStagiaire endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public RestaurationStagiaire status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public RestaurationStagiaire cardNumber(String cardNumber) {
        this.setCardNumber(cardNumber);
        return this;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Candidat getCandidat() {
        return this.candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
        this.candidatId = candidat != null ? candidat.getId() : null;
    }

    public RestaurationStagiaire candidat(Candidat candidat) {
        this.setCandidat(candidat);
        return this;
    }

    public Long getCandidatId() {
        return this.candidatId;
    }

    public void setCandidatId(Long candidat) {
        this.candidatId = candidat;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurationStagiaire)) {
            return false;
        }
        return getId() != null && getId().equals(((RestaurationStagiaire) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurationStagiaire{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", cardNumber='" + getCardNumber() + "'" +
            "}";
    }
}
