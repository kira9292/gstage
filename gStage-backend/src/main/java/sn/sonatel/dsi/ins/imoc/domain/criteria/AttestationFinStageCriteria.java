package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.AttestationFinStageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /attestation-fin-stages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationFinStageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private LocalDateFilter issueDate;

    private LocalDateFilter signatureDate;

    private StringFilter comments;

    private Boolean distinct;

    public AttestationFinStageCriteria() {}

    public AttestationFinStageCriteria(AttestationFinStageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.issueDate = other.optionalIssueDate().map(LocalDateFilter::copy).orElse(null);
        this.signatureDate = other.optionalSignatureDate().map(LocalDateFilter::copy).orElse(null);
        this.comments = other.optionalComments().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AttestationFinStageCriteria copy() {
        return new AttestationFinStageCriteria(this);
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

    public LocalDateFilter getIssueDate() {
        return issueDate;
    }

    public Optional<LocalDateFilter> optionalIssueDate() {
        return Optional.ofNullable(issueDate);
    }

    public LocalDateFilter issueDate() {
        if (issueDate == null) {
            setIssueDate(new LocalDateFilter());
        }
        return issueDate;
    }

    public void setIssueDate(LocalDateFilter issueDate) {
        this.issueDate = issueDate;
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
        final AttestationFinStageCriteria that = (AttestationFinStageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(issueDate, that.issueDate) &&
            Objects.equals(signatureDate, that.signatureDate) &&
            Objects.equals(comments, that.comments) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, issueDate, signatureDate, comments, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttestationFinStageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalIssueDate().map(f -> "issueDate=" + f + ", ").orElse("") +
            optionalSignatureDate().map(f -> "signatureDate=" + f + ", ").orElse("") +
            optionalComments().map(f -> "comments=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
