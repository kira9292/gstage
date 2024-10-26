package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.PaymentStatus;

/**
 * A EtatPaiement.
 */
@Table("etat_paiement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtatPaiement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("reference")
    private String reference;

    @NotNull(message = "must not be null")
    @Column("payment_number")
    private String paymentNumber;

    @NotNull(message = "must not be null")
    @Column("payment_date")
    private LocalDate paymentDate;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("amount")
    private Double amount;

    @NotNull(message = "must not be null")
    @Column("act_code")
    private String actCode;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[0-9]{9}$")
    @Column("payment_phone")
    private String paymentPhone;

    @NotNull(message = "must not be null")
    @Column("status")
    private PaymentStatus status;

    @Column("processing_date")
    private LocalDate processingDate;

    @Column("comments")
    private String comments;

    @Transient
    @JsonIgnoreProperties(value = { "validations", "contrat", "manager", "etatPaiement" }, allowSetters = true)
    private Set<AttestationPresence> attestationPresences = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "drh", "assistantGWTECreator", "candidat" }, allowSetters = true)
    private Contrat contrat;

    @Transient
    @JsonIgnoreProperties(value = { "etatPaiements" }, allowSetters = true)
    private Dfc dfc;

    @Transient
    @JsonIgnoreProperties(value = { "demandeStages", "contrats", "etatPaiements" }, allowSetters = true)
    private AssistantGWTE assistantGWTECreator;

    @Column("contrat_id")
    private Long contratId;

    @Column("dfc_id")
    private Long dfcId;

    @Column("assistantgwtecreator_id")
    private Long assistantGWTECreatorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EtatPaiement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public EtatPaiement reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPaymentNumber() {
        return this.paymentNumber;
    }

    public EtatPaiement paymentNumber(String paymentNumber) {
        this.setPaymentNumber(paymentNumber);
        return this;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public LocalDate getPaymentDate() {
        return this.paymentDate;
    }

    public EtatPaiement paymentDate(LocalDate paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getAmount() {
        return this.amount;
    }

    public EtatPaiement amount(Double amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getActCode() {
        return this.actCode;
    }

    public EtatPaiement actCode(String actCode) {
        this.setActCode(actCode);
        return this;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public String getPaymentPhone() {
        return this.paymentPhone;
    }

    public EtatPaiement paymentPhone(String paymentPhone) {
        this.setPaymentPhone(paymentPhone);
        return this;
    }

    public void setPaymentPhone(String paymentPhone) {
        this.paymentPhone = paymentPhone;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public EtatPaiement status(PaymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getProcessingDate() {
        return this.processingDate;
    }

    public EtatPaiement processingDate(LocalDate processingDate) {
        this.setProcessingDate(processingDate);
        return this;
    }

    public void setProcessingDate(LocalDate processingDate) {
        this.processingDate = processingDate;
    }

    public String getComments() {
        return this.comments;
    }

    public EtatPaiement comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Set<AttestationPresence> getAttestationPresences() {
        return this.attestationPresences;
    }

    public void setAttestationPresences(Set<AttestationPresence> attestationPresences) {
        if (this.attestationPresences != null) {
            this.attestationPresences.forEach(i -> i.setEtatPaiement(null));
        }
        if (attestationPresences != null) {
            attestationPresences.forEach(i -> i.setEtatPaiement(this));
        }
        this.attestationPresences = attestationPresences;
    }

    public EtatPaiement attestationPresences(Set<AttestationPresence> attestationPresences) {
        this.setAttestationPresences(attestationPresences);
        return this;
    }

    public EtatPaiement addAttestationPresences(AttestationPresence attestationPresence) {
        this.attestationPresences.add(attestationPresence);
        attestationPresence.setEtatPaiement(this);
        return this;
    }

    public EtatPaiement removeAttestationPresences(AttestationPresence attestationPresence) {
        this.attestationPresences.remove(attestationPresence);
        attestationPresence.setEtatPaiement(null);
        return this;
    }

    public Contrat getContrat() {
        return this.contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
        this.contratId = contrat != null ? contrat.getId() : null;
    }

    public EtatPaiement contrat(Contrat contrat) {
        this.setContrat(contrat);
        return this;
    }

    public Dfc getDfc() {
        return this.dfc;
    }

    public void setDfc(Dfc dfc) {
        this.dfc = dfc;
        this.dfcId = dfc != null ? dfc.getId() : null;
    }

    public EtatPaiement dfc(Dfc dfc) {
        this.setDfc(dfc);
        return this;
    }

    public AssistantGWTE getAssistantGWTECreator() {
        return this.assistantGWTECreator;
    }

    public void setAssistantGWTECreator(AssistantGWTE assistantGWTE) {
        this.assistantGWTECreator = assistantGWTE;
        this.assistantGWTECreatorId = assistantGWTE != null ? assistantGWTE.getId() : null;
    }

    public EtatPaiement assistantGWTECreator(AssistantGWTE assistantGWTE) {
        this.setAssistantGWTECreator(assistantGWTE);
        return this;
    }

    public Long getContratId() {
        return this.contratId;
    }

    public void setContratId(Long contrat) {
        this.contratId = contrat;
    }

    public Long getDfcId() {
        return this.dfcId;
    }

    public void setDfcId(Long dfc) {
        this.dfcId = dfc;
    }

    public Long getAssistantGWTECreatorId() {
        return this.assistantGWTECreatorId;
    }

    public void setAssistantGWTECreatorId(Long assistantGWTE) {
        this.assistantGWTECreatorId = assistantGWTE;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtatPaiement)) {
            return false;
        }
        return getId() != null && getId().equals(((EtatPaiement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtatPaiement{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", paymentNumber='" + getPaymentNumber() + "'" +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", amount=" + getAmount() +
            ", actCode='" + getActCode() + "'" +
            ", paymentPhone='" + getPaymentPhone() + "'" +
            ", status='" + getStatus() + "'" +
            ", processingDate='" + getProcessingDate() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
