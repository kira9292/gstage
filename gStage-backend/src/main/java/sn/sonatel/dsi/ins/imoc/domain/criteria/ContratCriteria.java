package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.Contrat} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.ContratResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /contrats?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContratCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ContractStatus
     */
    public static class ContractStatusFilter extends Filter<ContractStatus> {

        public ContractStatusFilter() {}

        public ContractStatusFilter(ContractStatusFilter filter) {
            super(filter);
        }

        @Override
        public ContractStatusFilter copy() {
            return new ContractStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private DoubleFilter compensation;

    private ContractStatusFilter status;

    private StringFilter assignmentSite;

    private LocalDateFilter signatureDate;

    private StringFilter comments;

    private LongFilter attestationFinStageId;

    private LongFilter drhId;

    private LongFilter assistantGWTECreatorId;

    private LongFilter candidatId;

    private Boolean distinct;

    public ContratCriteria() {}

    public ContratCriteria(ContratCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(LocalDateFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(LocalDateFilter::copy).orElse(null);
        this.compensation = other.optionalCompensation().map(DoubleFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ContractStatusFilter::copy).orElse(null);
        this.assignmentSite = other.optionalAssignmentSite().map(StringFilter::copy).orElse(null);
        this.signatureDate = other.optionalSignatureDate().map(LocalDateFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.attestationFinStageId = other.optionalAttestationFinStageId().map(LongFilter::copy).orElse(null);
        this.drhId = other.optionalDrhId().map(LongFilter::copy).orElse(null);
        this.assistantGWTECreatorId = other.optionalAssistantGWTECreatorId().map(LongFilter::copy).orElse(null);
        this.candidatId = other.optionalCandidatId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ContratCriteria copy() {
        return new ContratCriteria(this);
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

    public DoubleFilter getCompensation() {
        return compensation;
    }

    public Optional<DoubleFilter> optionalCompensation() {
        return Optional.ofNullable(compensation);
    }

    public DoubleFilter compensation() {
        if (compensation == null) {
            setCompensation(new DoubleFilter());
        }
        return compensation;
    }

    public void setCompensation(DoubleFilter compensation) {
        this.compensation = compensation;
    }

    public ContractStatusFilter getStatus() {
        return status;
    }

    public Optional<ContractStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ContractStatusFilter status() {
        if (status == null) {
            setStatus(new ContractStatusFilter());
        }
        return status;
    }

    public void setStatus(ContractStatusFilter status) {
        this.status = status;
    }

    public StringFilter getAssignmentSite() {
        return assignmentSite;
    }

    public Optional<StringFilter> optionalAssignmentSite() {
        return Optional.ofNullable(assignmentSite);
    }

    public StringFilter assignmentSite() {
        if (assignmentSite == null) {
            setAssignmentSite(new StringFilter());
        }
        return assignmentSite;
    }

    public void setAssignmentSite(StringFilter assignmentSite) {
        this.assignmentSite = assignmentSite;
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

    public LongFilter getDrhId() {
        return drhId;
    }

    public Optional<LongFilter> optionalDrhId() {
        return Optional.ofNullable(drhId);
    }

    public LongFilter drhId() {
        if (drhId == null) {
            setDrhId(new LongFilter());
        }
        return drhId;
    }

    public void setDrhId(LongFilter drhId) {
        this.drhId = drhId;
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

    public LongFilter getCandidatId() {
        return candidatId;
    }

    public Optional<LongFilter> optionalCandidatId() {
        return Optional.ofNullable(candidatId);
    }

    public LongFilter candidatId() {
        if (candidatId == null) {
            setCandidatId(new LongFilter());
        }
        return candidatId;
    }

    public void setCandidatId(LongFilter candidatId) {
        this.candidatId = candidatId;
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
        final ContratCriteria that = (ContratCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(compensation, that.compensation) &&
            Objects.equals(status, that.status) &&
            Objects.equals(assignmentSite, that.assignmentSite) &&
            Objects.equals(signatureDate, that.signatureDate) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(attestationFinStageId, that.attestationFinStageId) &&
            Objects.equals(drhId, that.drhId) &&
            Objects.equals(assistantGWTECreatorId, that.assistantGWTECreatorId) &&
            Objects.equals(candidatId, that.candidatId) &&
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
            compensation,
            status,
            assignmentSite,
            signatureDate,
            comments,
            attestationFinStageId,
            drhId,
            assistantGWTECreatorId,
            candidatId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContratCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalCompensation().map(f -> "compensation=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalAssignmentSite().map(f -> "assignmentSite=" + f + ", ").orElse("") +
            optionalSignatureDate().map(f -> "signatureDate=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalAttestationFinStageId().map(f -> "attestationFinStageId=" + f + ", ").orElse("") +
            optionalDrhId().map(f -> "drhId=" + f + ", ").orElse("") +
            optionalAssistantGWTECreatorId().map(f -> "assistantGWTECreatorId=" + f + ", ").orElse("") +
            optionalCandidatId().map(f -> "candidatId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
