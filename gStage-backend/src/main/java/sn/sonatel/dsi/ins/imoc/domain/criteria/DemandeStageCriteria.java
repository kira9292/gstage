package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.DemandeStage} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.DemandeStageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /demande-stages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeStageCriteria implements Serializable, Criteria {

    /**
     * Class for filtering InternshipStatus
     */
    public static class InternshipStatusFilter extends Filter<InternshipStatus> {

        public InternshipStatusFilter() {}

        public InternshipStatusFilter(InternshipStatusFilter filter) {
            super(filter);
        }

        @Override
        public InternshipStatusFilter copy() {
            return new InternshipStatusFilter(this);
        }
    }

    /**
     * Class for filtering InternshipType
     */
    public static class InternshipTypeFilter extends Filter<InternshipType> {

        public InternshipTypeFilter() {}

        public InternshipTypeFilter(InternshipTypeFilter filter) {
            super(filter);
        }

        @Override
        public InternshipTypeFilter copy() {
            return new InternshipTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private LocalDateFilter creationDate;

    private InternshipStatusFilter status;

    private InternshipTypeFilter internshipType;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private BooleanFilter validated;

    private LongFilter candidatId;

    private LongFilter assistantGWTEId;

    private LongFilter managerId;

    private LongFilter departementId;

    private LongFilter businessUnitId;

    private Boolean distinct;

    public DemandeStageCriteria() {}

    public DemandeStageCriteria(DemandeStageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.creationDate = other.optionalCreationDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(InternshipStatusFilter::copy).orElse(null);
        this.internshipType = other.optionalInternshipType().map(InternshipTypeFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(LocalDateFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(LocalDateFilter::copy).orElse(null);
        this.validated = other.optionalValidated().map(BooleanFilter::copy).orElse(null);
        this.candidatId = other.optionalCandidatId().map(LongFilter::copy).orElse(null);
        this.assistantGWTEId = other.optionalAssistantGWTEId().map(LongFilter::copy).orElse(null);
        this.managerId = other.optionalManagerId().map(LongFilter::copy).orElse(null);
        this.departementId = other.optionalDepartementId().map(LongFilter::copy).orElse(null);
        this.businessUnitId = other.optionalBusinessUnitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DemandeStageCriteria copy() {
        return new DemandeStageCriteria(this);
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

    public LocalDateFilter getCreationDate() {
        return creationDate;
    }

    public Optional<LocalDateFilter> optionalCreationDate() {
        return Optional.ofNullable(creationDate);
    }

    public LocalDateFilter creationDate() {
        if (creationDate == null) {
            setCreationDate(new LocalDateFilter());
        }
        return creationDate;
    }

    public void setCreationDate(LocalDateFilter creationDate) {
        this.creationDate = creationDate;
    }

    public InternshipStatusFilter getStatus() {
        return status;
    }

    public Optional<InternshipStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public InternshipStatusFilter status() {
        if (status == null) {
            setStatus(new InternshipStatusFilter());
        }
        return status;
    }

    public void setStatus(InternshipStatusFilter status) {
        this.status = status;
    }

    public InternshipTypeFilter getInternshipType() {
        return internshipType;
    }

    public Optional<InternshipTypeFilter> optionalInternshipType() {
        return Optional.ofNullable(internshipType);
    }

    public InternshipTypeFilter internshipType() {
        if (internshipType == null) {
            setInternshipType(new InternshipTypeFilter());
        }
        return internshipType;
    }

    public void setInternshipType(InternshipTypeFilter internshipType) {
        this.internshipType = internshipType;
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

    public BooleanFilter getValidated() {
        return validated;
    }

    public Optional<BooleanFilter> optionalValidated() {
        return Optional.ofNullable(validated);
    }

    public BooleanFilter validated() {
        if (validated == null) {
            setValidated(new BooleanFilter());
        }
        return validated;
    }

    public void setValidated(BooleanFilter validated) {
        this.validated = validated;
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

    public LongFilter getAssistantGWTEId() {
        return assistantGWTEId;
    }

    public Optional<LongFilter> optionalAssistantGWTEId() {
        return Optional.ofNullable(assistantGWTEId);
    }

    public LongFilter assistantGWTEId() {
        if (assistantGWTEId == null) {
            setAssistantGWTEId(new LongFilter());
        }
        return assistantGWTEId;
    }

    public void setAssistantGWTEId(LongFilter assistantGWTEId) {
        this.assistantGWTEId = assistantGWTEId;
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

    public LongFilter getDepartementId() {
        return departementId;
    }

    public Optional<LongFilter> optionalDepartementId() {
        return Optional.ofNullable(departementId);
    }

    public LongFilter departementId() {
        if (departementId == null) {
            setDepartementId(new LongFilter());
        }
        return departementId;
    }

    public void setDepartementId(LongFilter departementId) {
        this.departementId = departementId;
    }

    public LongFilter getBusinessUnitId() {
        return businessUnitId;
    }

    public Optional<LongFilter> optionalBusinessUnitId() {
        return Optional.ofNullable(businessUnitId);
    }

    public LongFilter businessUnitId() {
        if (businessUnitId == null) {
            setBusinessUnitId(new LongFilter());
        }
        return businessUnitId;
    }

    public void setBusinessUnitId(LongFilter businessUnitId) {
        this.businessUnitId = businessUnitId;
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
        final DemandeStageCriteria that = (DemandeStageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(creationDate, that.creationDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(internshipType, that.internshipType) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(validated, that.validated) &&
            Objects.equals(candidatId, that.candidatId) &&
            Objects.equals(assistantGWTEId, that.assistantGWTEId) &&
            Objects.equals(managerId, that.managerId) &&
            Objects.equals(departementId, that.departementId) &&
            Objects.equals(businessUnitId, that.businessUnitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            creationDate,
            status,
            internshipType,
            startDate,
            endDate,
            validated,
            candidatId,
            assistantGWTEId,
            managerId,
            departementId,
            businessUnitId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeStageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalCreationDate().map(f -> "creationDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalInternshipType().map(f -> "internshipType=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalValidated().map(f -> "validated=" + f + ", ").orElse("") +
            optionalCandidatId().map(f -> "candidatId=" + f + ", ").orElse("") +
            optionalAssistantGWTEId().map(f -> "assistantGWTEId=" + f + ", ").orElse("") +
            optionalManagerId().map(f -> "managerId=" + f + ", ").orElse("") +
            optionalDepartementId().map(f -> "departementId=" + f + ", ").orElse("") +
            optionalBusinessUnitId().map(f -> "businessUnitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
