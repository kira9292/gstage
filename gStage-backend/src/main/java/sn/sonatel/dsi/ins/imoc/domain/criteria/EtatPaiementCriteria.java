package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.PaymentStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.EtatPaiement} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.EtatPaiementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /etat-paiements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtatPaiementCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PaymentStatus
     */
    public static class PaymentStatusFilter extends Filter<PaymentStatus> {

        public PaymentStatusFilter() {}

        public PaymentStatusFilter(PaymentStatusFilter filter) {
            super(filter);
        }

        @Override
        public PaymentStatusFilter copy() {
            return new PaymentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private StringFilter paymentNumber;

    private LocalDateFilter paymentDate;

    private DoubleFilter amount;

    private StringFilter actCode;

    private StringFilter paymentPhone;

    private PaymentStatusFilter status;

    private LocalDateFilter processingDate;

    private StringFilter comments;

    private LongFilter contratId;

    private LongFilter dfcId;

    private LongFilter assistantGWTECreatorId;

    private Boolean distinct;

    public EtatPaiementCriteria() {}

    public EtatPaiementCriteria(EtatPaiementCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.paymentNumber = other.optionalPaymentNumber().map(StringFilter::copy).orElse(null);
        this.paymentDate = other.optionalPaymentDate().map(LocalDateFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(DoubleFilter::copy).orElse(null);
        this.actCode = other.optionalActCode().map(StringFilter::copy).orElse(null);
        this.paymentPhone = other.optionalPaymentPhone().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(PaymentStatusFilter::copy).orElse(null);
        this.processingDate = other.optionalProcessingDate().map(LocalDateFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.contratId = other.optionalContratId().map(LongFilter::copy).orElse(null);
        this.dfcId = other.optionalDfcId().map(LongFilter::copy).orElse(null);
        this.assistantGWTECreatorId = other.optionalAssistantGWTECreatorId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EtatPaiementCriteria copy() {
        return new EtatPaiementCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public StringFilter getPaymentNumber() {
        return paymentNumber;
    }

    public Optional<StringFilter> optionalPaymentNumber() {
        return Optional.ofNullable(paymentNumber);
    }

    public StringFilter paymentNumber() {
        if (paymentNumber == null) {
            setPaymentNumber(new StringFilter());
        }
        return paymentNumber;
    }

    public void setPaymentNumber(StringFilter paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public LocalDateFilter getPaymentDate() {
        return paymentDate;
    }

    public Optional<LocalDateFilter> optionalPaymentDate() {
        return Optional.ofNullable(paymentDate);
    }

    public LocalDateFilter paymentDate() {
        if (paymentDate == null) {
            setPaymentDate(new LocalDateFilter());
        }
        return paymentDate;
    }

    public void setPaymentDate(LocalDateFilter paymentDate) {
        this.paymentDate = paymentDate;
    }

    public DoubleFilter getAmount() {
        return amount;
    }

    public Optional<DoubleFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public DoubleFilter amount() {
        if (amount == null) {
            setAmount(new DoubleFilter());
        }
        return amount;
    }

    public void setAmount(DoubleFilter amount) {
        this.amount = amount;
    }

    public StringFilter getActCode() {
        return actCode;
    }

    public Optional<StringFilter> optionalActCode() {
        return Optional.ofNullable(actCode);
    }

    public StringFilter actCode() {
        if (actCode == null) {
            setActCode(new StringFilter());
        }
        return actCode;
    }

    public void setActCode(StringFilter actCode) {
        this.actCode = actCode;
    }

    public StringFilter getPaymentPhone() {
        return paymentPhone;
    }

    public Optional<StringFilter> optionalPaymentPhone() {
        return Optional.ofNullable(paymentPhone);
    }

    public StringFilter paymentPhone() {
        if (paymentPhone == null) {
            setPaymentPhone(new StringFilter());
        }
        return paymentPhone;
    }

    public void setPaymentPhone(StringFilter paymentPhone) {
        this.paymentPhone = paymentPhone;
    }

    public PaymentStatusFilter getStatus() {
        return status;
    }

    public Optional<PaymentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public PaymentStatusFilter status() {
        if (status == null) {
            setStatus(new PaymentStatusFilter());
        }
        return status;
    }

    public void setStatus(PaymentStatusFilter status) {
        this.status = status;
    }

    public LocalDateFilter getProcessingDate() {
        return processingDate;
    }

    public Optional<LocalDateFilter> optionalProcessingDate() {
        return Optional.ofNullable(processingDate);
    }

    public LocalDateFilter processingDate() {
        if (processingDate == null) {
            setProcessingDate(new LocalDateFilter());
        }
        return processingDate;
    }

    public void setProcessingDate(LocalDateFilter processingDate) {
        this.processingDate = processingDate;
    }

    public StringFilter getComments() {
        return comments;
    }

    public Optional<StringFilter> optionalComments() {
        return Optional.ofNullable(comments);
    }

    public StringFilter comments() {
        if (comments == null) {
            setComments(new StringFilter());
        }
        return comments;
    }

    public void setComments(StringFilter comments) {
        this.comments = comments;
    }

    public LongFilter getContratId() {
        return contratId;
    }

    public Optional<LongFilter> optionalContratId() {
        return Optional.ofNullable(contratId);
    }

    public LongFilter contratId() {
        if (contratId == null) {
            setContratId(new LongFilter());
        }
        return contratId;
    }

    public void setContratId(LongFilter contratId) {
        this.contratId = contratId;
    }

    public LongFilter getDfcId() {
        return dfcId;
    }

    public Optional<LongFilter> optionalDfcId() {
        return Optional.ofNullable(dfcId);
    }

    public LongFilter dfcId() {
        if (dfcId == null) {
            setDfcId(new LongFilter());
        }
        return dfcId;
    }

    public void setDfcId(LongFilter dfcId) {
        this.dfcId = dfcId;
    }

    public LongFilter getAssistantGWTECreatorId() {
        return assistantGWTECreatorId;
    }

    public Optional<LongFilter> optionalAssistantGWTECreatorId() {
        return Optional.ofNullable(assistantGWTECreatorId);
    }

    public LongFilter assistantGWTECreatorId() {
        if (assistantGWTECreatorId == null) {
            setAssistantGWTECreatorId(new LongFilter());
        }
        return assistantGWTECreatorId;
    }

    public void setAssistantGWTECreatorId(LongFilter assistantGWTECreatorId) {
        this.assistantGWTECreatorId = assistantGWTECreatorId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EtatPaiementCriteria that = (EtatPaiementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(paymentNumber, that.paymentNumber) &&
            Objects.equals(paymentDate, that.paymentDate) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(actCode, that.actCode) &&
            Objects.equals(paymentPhone, that.paymentPhone) &&
            Objects.equals(status, that.status) &&
            Objects.equals(processingDate, that.processingDate) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(contratId, that.contratId) &&
            Objects.equals(dfcId, that.dfcId) &&
            Objects.equals(assistantGWTECreatorId, that.assistantGWTECreatorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            paymentNumber,
            paymentDate,
            amount,
            actCode,
            paymentPhone,
            status,
            processingDate,
            comments,
            contratId,
            dfcId,
            assistantGWTECreatorId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtatPaiementCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalPaymentNumber().map(f -> "paymentNumber=" + f + ", ").orElse("") +
            optionalPaymentDate().map(f -> "paymentDate=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalActCode().map(f -> "actCode=" + f + ", ").orElse("") +
            optionalPaymentPhone().map(f -> "paymentPhone=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalProcessingDate().map(f -> "processingDate=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalContratId().map(f -> "contratId=" + f + ", ").orElse("") +
            optionalDfcId().map(f -> "dfcId=" + f + ", ").orElse("") +
            optionalAssistantGWTECreatorId().map(f -> "assistantGWTECreatorId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
