package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.RestaurationStagiaireResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /restauration-stagiaires?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurationStagiaireCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private BooleanFilter status;

    private StringFilter cardNumber;

    private LongFilter candidatId;

    private Boolean distinct;

    public RestaurationStagiaireCriteria() {}

    public RestaurationStagiaireCriteria(RestaurationStagiaireCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(LocalDateFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(BooleanFilter::copy).orElse(null);
        this.cardNumber = other.optionalCardNumber().map(StringFilter::copy).orElse(null);
        this.candidatId = other.optionalCandidatId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RestaurationStagiaireCriteria copy() {
        return new RestaurationStagiaireCriteria(this);
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

    public StringFilter getCardNumber() {
        return cardNumber;
    }

    public Optional<StringFilter> optionalCardNumber() {
        return Optional.ofNullable(cardNumber);
    }

    public StringFilter cardNumber() {
        if (cardNumber == null) {
            setCardNumber(new StringFilter());
        }
        return cardNumber;
    }

    public void setCardNumber(StringFilter cardNumber) {
        this.cardNumber = cardNumber;
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
        final RestaurationStagiaireCriteria that = (RestaurationStagiaireCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(cardNumber, that.cardNumber) &&
            Objects.equals(candidatId, that.candidatId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, status, cardNumber, candidatId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurationStagiaireCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCardNumber().map(f -> "cardNumber=" + f + ", ").orElse("") +
            optionalCandidatId().map(f -> "candidatId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
