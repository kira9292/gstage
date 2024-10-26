package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.PaymentStatus;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.EtatPaiement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtatPaiementDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String reference;

    @NotNull(message = "must not be null")
    private String paymentNumber;

    @NotNull(message = "must not be null")
    private LocalDate paymentDate;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private Double amount;

    @NotNull(message = "must not be null")
    private String actCode;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[0-9]{9}$")
    private String paymentPhone;

    @NotNull(message = "must not be null")
    private PaymentStatus status;

    private LocalDate processingDate;

    private String comments;

    private ContratDTO contrat;

    private DfcDTO dfc;

    private AssistantGWTEDTO assistantGWTECreator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public String getPaymentPhone() {
        return paymentPhone;
    }

    public void setPaymentPhone(String paymentPhone) {
        this.paymentPhone = paymentPhone;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(LocalDate processingDate) {
        this.processingDate = processingDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ContratDTO getContrat() {
        return contrat;
    }

    public void setContrat(ContratDTO contrat) {
        this.contrat = contrat;
    }

    public DfcDTO getDfc() {
        return dfc;
    }

    public void setDfc(DfcDTO dfc) {
        this.dfc = dfc;
    }

    public AssistantGWTEDTO getAssistantGWTECreator() {
        return assistantGWTECreator;
    }

    public void setAssistantGWTECreator(AssistantGWTEDTO assistantGWTECreator) {
        this.assistantGWTECreator = assistantGWTECreator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtatPaiementDTO)) {
            return false;
        }

        EtatPaiementDTO etatPaiementDTO = (EtatPaiementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, etatPaiementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtatPaiementDTO{" +
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
            ", contrat=" + getContrat() +
            ", dfc=" + getDfc() +
            ", assistantGWTECreator=" + getAssistantGWTECreator() +
            "}";
    }
}
