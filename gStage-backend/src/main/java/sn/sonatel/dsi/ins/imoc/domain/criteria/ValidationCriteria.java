package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ValidationStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.Validation} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.ValidationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /validations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ValidationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ValidationStatus
     */
    public static class ValidationStatusFilter extends Filter<ValidationStatus> {

        public ValidationStatusFilter() {}

        public ValidationStatusFilter(ValidationStatusFilter filter) {
            super(filter);
        }

        @Override
        public ValidationStatusFilter copy() {
            return new ValidationStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private LocalDateFilter validationDate;

    private ValidationStatusFilter status;

    private StringFilter comments;

    private StringFilter validatedBy;

    private LongFilter attestationPresenceId;

    private LongFilter contratId;

    private LongFilter attestationFinStageId;

    private LongFilter userId;

    private Boolean distinct;

    public ValidationCriteria() {}

    public ValidationCriteria(ValidationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.validationDate = other.optionalValidationDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ValidationStatusFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.validatedBy = other.optionalValidatedBy().map(StringFilter::copy).orElse(null);
        this.attestationPresenceId = other.optionalAttestationPresenceId().map(LongFilter::copy).orElse(null);
        this.contratId = other.optionalContratId().map(LongFilter::copy).orElse(null);
        this.attestationFinStageId = other.optionalAttestationFinStageId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ValidationCriteria copy() {
        return new ValidationCriteria(this);
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

    public LocalDateFilter getValidationDate() {
        return validationDate;
    }

    public Optional<LocalDateFilter> optionalValidationDate() {
        return Optional.ofNullable(validationDate);
    }

    public LocalDateFilter validationDate() {
        if (validationDate == null) {
            setValidationDate(new LocalDateFilter());
        }
        return validationDate;
    }

    public void setValidationDate(LocalDateFilter validationDate) {
        this.validationDate = validationDate;
    }

    public ValidationStatusFilter getStatus() {
        return status;
    }

    public Optional<ValidationStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ValidationStatusFilter status() {
        if (status == null) {
            setStatus(new ValidationStatusFilter());
        }
        return status;
    }

    public void setStatus(ValidationStatusFilter status) {
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

    public StringFilter getValidatedBy() {
        return validatedBy;
    }

    public Optional<StringFilter> optionalValidatedBy() {
        return Optional.ofNullable(validatedBy);
    }

    public StringFilter validatedBy() {
        if (validatedBy == null) {
            setValidatedBy(new StringFilter());
        }
        return validatedBy;
    }

    public void setValidatedBy(StringFilter validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LongFilter getAttestationPresenceId() {
        return attestationPresenceId;
    }

    public Optional<LongFilter> optionalAttestationPresenceId() {
        return Optional.ofNullable(attestationPresenceId);
    }

    public LongFilter attestationPresenceId() {
        if (attestationPresenceId == null) {
            setAttestationPresenceId(new LongFilter());
        }
        return attestationPresenceId;
    }

    public void setAttestationPresenceId(LongFilter attestationPresenceId) {
        this.attestationPresenceId = attestationPresenceId;
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

    public LongFilter getAttestationFinStageId() {
        return attestationFinStageId;
    }

    public Optional<LongFilter> optionalAttestationFinStageId() {
        return Optional.ofNullable(attestationFinStageId);
    }

    public LongFilter attestationFinStageId() {
        if (attestationFinStageId == null) {
            setAttestationFinStageId(new LongFilter());
        }
        return attestationFinStageId;
    }

    public void setAttestationFinStageId(LongFilter attestationFinStageId) {
        this.attestationFinStageId = attestationFinStageId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final ValidationCriteria that = (ValidationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(validationDate, that.validationDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(validatedBy, that.validatedBy) &&
            Objects.equals(attestationPresenceId, that.attestationPresenceId) &&
            Objects.equals(contratId, that.contratId) &&
            Objects.equals(attestationFinStageId, that.attestationFinStageId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            validationDate,
            status,
            comments,
            validatedBy,
            attestationPresenceId,
            contratId,
            attestationFinStageId,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ValidationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalValidationDate().map(f -> "validationDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalValidatedBy().map(f -> "validatedBy=" + f + ", ").orElse("") +
            optionalAttestationPresenceId().map(f -> "attestationPresenceId=" + f + ", ").orElse("") +
            optionalContratId().map(f -> "contratId=" + f + ", ").orElse("") +
            optionalAttestationFinStageId().map(f -> "attestationFinStageId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
