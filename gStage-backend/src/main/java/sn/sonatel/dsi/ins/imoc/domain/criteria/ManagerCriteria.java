package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.Manager} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.ManagerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /managers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ManagerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phone;

    private LongFilter serviceId;

    private Boolean distinct;

    public ManagerCriteria() {}

    public ManagerCriteria(ManagerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.serviceId = other.optionalServiceId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ManagerCriteria copy() {
        return new ManagerCriteria(this);
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

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public LongFilter getServiceId() {
        return serviceId;
    }

    public Optional<LongFilter> optionalServiceId() {
        return Optional.ofNullable(serviceId);
    }

    public LongFilter serviceId() {
        if (serviceId == null) {
            setServiceId(new LongFilter());
        }
        return serviceId;
    }

    public void setServiceId(LongFilter serviceId) {
        this.serviceId = serviceId;
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
        final ManagerCriteria that = (ManagerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(serviceId, that.serviceId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, serviceId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalServiceId().map(f -> "serviceId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
