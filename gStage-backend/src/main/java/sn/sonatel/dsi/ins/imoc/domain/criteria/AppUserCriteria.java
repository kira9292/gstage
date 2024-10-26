package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.AppUser} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.AppUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /app-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter username;

    private StringFilter email;

    private StringFilter password;

    private StringFilter name;

    private StringFilter firstName;

    private Boolean distinct;

    public AppUserCriteria() {}

    public AppUserCriteria(AppUserCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.username = other.optionalUsername().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.password = other.optionalPassword().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.firstName = other.optionalFirstName().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AppUserCriteria copy() {
        return new AppUserCriteria(this);
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

    public StringFilter getUsername() {
        return username;
    }

    public Optional<StringFilter> optionalUsername() {
        return Optional.ofNullable(username);
    }

    public StringFilter username() {
        if (username == null) {
            setUsername(new StringFilter());
        }
        return username;
    }

    public void setUsername(StringFilter username) {
        this.username = username;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPassword() {
        return password;
    }

    public Optional<StringFilter> optionalPassword() {
        return Optional.ofNullable(password);
    }

    public StringFilter password() {
        if (password == null) {
            setPassword(new StringFilter());
        }
        return password;
    }

    public void setPassword(StringFilter password) {
        this.password = password;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public Optional<StringFilter> optionalFirstName() {
        return Optional.ofNullable(firstName);
    }

    public StringFilter firstName() {
        if (firstName == null) {
            setFirstName(new StringFilter());
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
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
        final AppUserCriteria that = (AppUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(username, that.username) &&
            Objects.equals(email, that.email) &&
            Objects.equals(password, that.password) &&
            Objects.equals(name, that.name) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, name, firstName, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUsername().map(f -> "username=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPassword().map(f -> "password=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalFirstName().map(f -> "firstName=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
