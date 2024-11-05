package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.PaymentStatus;

/**
 * A EtatPaiement.
 */
@Entity
@Table(name = "etat_paiement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtatPaiement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_number")
    private String paymentNumber;

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "amount", nullable = false)
    private Double amount;

    @NotNull
    @Column(name = "act_code", nullable = false)
    private String actCode;

    @NotNull
    @Pattern(regexp = "^[0-9]{9}$")
    @Column(name = "payment_phone", nullable = false)
    private String paymentPhone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "processing_date")
    private LocalDate processingDate;

    @Column(name = "comments")
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "service",
            "attestationFinStage",
            "etatPaiements",
            "contrats",
            "demandeStages",
            "attestationPresences",
            "candidats",
            "validations",
            "roles",
            "validationStatusUser",
            "restaurationStagiaires",
        },
        allowSetters = true
    )
    private AppUser appUser;

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

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public EtatPaiement appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
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
