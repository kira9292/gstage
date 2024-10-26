package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.AttestationPresence} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.AttestationPresenceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /attestation-presences?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationPresenceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private LocalDateFilter signatureDate;

    private BooleanFilter status;

    private StringFilter comments;

    private LongFilter contratId;

    private LongFilter managerId;

    private LongFilter etatPaiementId;

    private Boolean distinct;

    public AttestationPresenceCriteria() {}

    public AttestationPresenceCriteria(AttestationPresenceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(LocalDateFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(LocalDateFilter::copy).orElse(null);
        this.signatureDate = other.optionalSignatureDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(BooleanFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.contratId = other.optionalContratId().map(LongFilter::copy).orElse(null);
        this.managerId = other.optionalManagerId().map(LongFilter::copy).orElse(null);
        this.etatPaiementId = other.optionalEtatPaiementId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AttestationPresenceCriteria copy() {
        return new AttestationPresenceCriteria(this);
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

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public Optional<LocalDateFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public LocalDateFilter startDate() {
        if (startDate == null) {
            setStartDate(new LocalDateFilter());
        }
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public LocalDateFilter getEndDate() {
        return endDate;
    }

    public Optional<LocalDateFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public LocalDateFilter endDate() {
        if (endDate == null) {
            setEndDate(new LocalDateFilter());
        }
        return endDate;
    }

    public void setEndDate(LocalDateFilter endDate) {
        this.endDate = endDate;
    }

    public LocalDateFilter getSignatureDate() {
        return signatureDate;
    }

    public Optional<LocalDateFilter> optionalSignatureDate() {
        return Optional.ofNullable(signatureDate);
    }

    public LocalDateFilter signatureDate() {
        if (signatureDate == null) {
            setSignatureDate(new LocalDateFilter());
        }
        return signatureDate;
    }

    public void setSignatureDate(LocalDateFilter signatureDate) {
        this.signatureDate = signatureDate;
    }

    public BooleanFilter getStatus() {
        return status;
    }

    public Optional<BooleanFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public BooleanFilter status() {
        if (status == null) {
            setStatus(new BooleanFilter());
        }
        return status;
    }

    public void setStatus(BooleanFilter status) {
        this.status = status;
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

    public LongFilter getManagerId() {
        return managerId;
    }

    public Optional<LongFilter> optionalManagerId() {
        return Optional.ofNullable(managerId);
    }

    public LongFilter managerId() {
        if (managerId == null) {
            setManagerId(new LongFilter());
        }
        return managerId;
    }

    public void setManagerId(LongFilter managerId) {
        this.managerId = managerId;
    }

    public LongFilter getEtatPaiementId() {
        return etatPaiementId;
    }

    public Optional<LongFilter> optionalEtatPaiementId() {
        return Optional.ofNullable(etatPaiementId);
    }

    public LongFilter etatPaiementId() {
        if (etatPaiementId == null) {
            setEtatPaiementId(new LongFilter());
        }
        return etatPaiementId;
    }

    public void setEtatPaiementId(LongFilter etatPaiementId) {
        this.etatPaiementId = etatPaiementId;
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
        final AttestationPresenceCriteria that = (AttestationPresenceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(signatureDate, that.signatureDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(contratId, that.contratId) &&
            Objects.equals(managerId, that.managerId) &&
            Objects.equals(etatPaiementId, that.etatPaiementId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            startDate,
            endDate,
            signatureDate,
            status,
            comments,
            contratId,
            managerId,
            etatPaiementId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttestationPresenceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalSignatureDate().map(f -> "signatureDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalContratId().map(f -> "contratId=" + f + ", ").orElse("") +
            optionalManagerId().map(f -> "managerId=" + f + ", ").orElse("") +
            optionalEtatPaiementId().map(f -> "etatPaiementId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
