package sn.sonatel.dsi.ins.imoc.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.CandidateStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.EducationLevel;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.sonatel.dsi.ins.imoc.domain.Candidat} entity. This class is used
 * in {@link sn.sonatel.dsi.ins.imoc.web.rest.CandidatResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /candidats?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CandidatCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EducationLevel
     */
    public static class EducationLevelFilter extends Filter<EducationLevel> {

        public EducationLevelFilter() {}

        public EducationLevelFilter(EducationLevelFilter filter) {
            super(filter);
        }

        @Override
        public EducationLevelFilter copy() {
            return new EducationLevelFilter(this);
        }
    }

    /**
     * Class for filtering CandidateStatus
     */
    public static class CandidateStatusFilter extends Filter<CandidateStatus> {

        public CandidateStatusFilter() {}

        public CandidateStatusFilter(CandidateStatusFilter filter) {
            super(filter);
        }

        @Override
        public CandidateStatusFilter copy() {
            return new CandidateStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter firstName;

    private StringFilter lastName;

    private LocalDateFilter birthDate;

    private StringFilter nationality;

    private StringFilter birthPlace;

    private StringFilter idNumber;

    private StringFilter address;

    private StringFilter email;

    private StringFilter phone;

    private EducationLevelFilter educationLevel;

    private StringFilter school;

    private StringFilter registrationNumber;

    private StringFilter currentEducation;

    private CandidateStatusFilter status;

    private LongFilter managerId;

    private Boolean distinct;

    public CandidatCriteria() {}

    public CandidatCriteria(CandidatCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.firstName = other.optionalFirstName().map(StringFilter::copy).orElse(null);
        this.lastName = other.optionalLastName().map(StringFilter::copy).orElse(null);
        this.birthDate = other.optionalBirthDate().map(LocalDateFilter::copy).orElse(null);
        this.nationality = other.optionalNationality().map(StringFilter::copy).orElse(null);
        this.birthPlace = other.optionalBirthPlace().map(StringFilter::copy).orElse(null);
        this.idNumber = other.optionalIdNumber().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.educationLevel = other.optionalEducationLevel().map(EducationLevelFilter::copy).orElse(null);
        this.school = other.optionalSchool().map(StringFilter::copy).orElse(null);
        this.registrationNumber = other.optionalRegistrationNumber().map(StringFilter::copy).orElse(null);
        this.currentEducation = other.optionalCurrentEducation().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(CandidateStatusFilter::copy).orElse(null);
        this.managerId = other.optionalManagerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CandidatCriteria copy() {
        return new CandidatCriteria(this);
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

    public StringFilter getLastName() {
        return lastName;
    }

    public Optional<StringFilter> optionalLastName() {
        return Optional.ofNullable(lastName);
    }

    public StringFilter lastName() {
        if (lastName == null) {
            setLastName(new StringFilter());
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public LocalDateFilter getBirthDate() {
        return birthDate;
    }

    public Optional<LocalDateFilter> optionalBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public LocalDateFilter birthDate() {
        if (birthDate == null) {
            setBirthDate(new LocalDateFilter());
        }
        return birthDate;
    }

    public void setBirthDate(LocalDateFilter birthDate) {
        this.birthDate = birthDate;
    }

    public StringFilter getNationality() {
        return nationality;
    }

    public Optional<StringFilter> optionalNationality() {
        return Optional.ofNullable(nationality);
    }

    public StringFilter nationality() {
        if (nationality == null) {
            setNationality(new StringFilter());
        }
        return nationality;
    }

    public void setNationality(StringFilter nationality) {
        this.nationality = nationality;
    }

    public StringFilter getBirthPlace() {
        return birthPlace;
    }

    public Optional<StringFilter> optionalBirthPlace() {
        return Optional.ofNullable(birthPlace);
    }

    public StringFilter birthPlace() {
        if (birthPlace == null) {
            setBirthPlace(new StringFilter());
        }
        return birthPlace;
    }

    public void setBirthPlace(StringFilter birthPlace) {
        this.birthPlace = birthPlace;
    }

    public StringFilter getIdNumber() {
        return idNumber;
    }

    public Optional<StringFilter> optionalIdNumber() {
        return Optional.ofNullable(idNumber);
    }

    public StringFilter idNumber() {
        if (idNumber == null) {
            setIdNumber(new StringFilter());
        }
        return idNumber;
    }

    public void setIdNumber(StringFilter idNumber) {
        this.idNumber = idNumber;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
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

    public EducationLevelFilter getEducationLevel() {
        return educationLevel;
    }

    public Optional<EducationLevelFilter> optionalEducationLevel() {
        return Optional.ofNullable(educationLevel);
    }

    public EducationLevelFilter educationLevel() {
        if (educationLevel == null) {
            setEducationLevel(new EducationLevelFilter());
        }
        return educationLevel;
    }

    public void setEducationLevel(EducationLevelFilter educationLevel) {
        this.educationLevel = educationLevel;
    }

    public StringFilter getSchool() {
        return school;
    }

    public Optional<StringFilter> optionalSchool() {
        return Optional.ofNullable(school);
    }

    public StringFilter school() {
        if (school == null) {
            setSchool(new StringFilter());
        }
        return school;
    }

    public void setSchool(StringFilter school) {
        this.school = school;
    }

    public StringFilter getRegistrationNumber() {
        return registrationNumber;
    }

    public Optional<StringFilter> optionalRegistrationNumber() {
        return Optional.ofNullable(registrationNumber);
    }

    public StringFilter registrationNumber() {
        if (registrationNumber == null) {
            setRegistrationNumber(new StringFilter());
        }
        return registrationNumber;
    }

    public void setRegistrationNumber(StringFilter registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public StringFilter getCurrentEducation() {
        return currentEducation;
    }

    public Optional<StringFilter> optionalCurrentEducation() {
        return Optional.ofNullable(currentEducation);
    }

    public StringFilter currentEducation() {
        if (currentEducation == null) {
            setCurrentEducation(new StringFilter());
        }
        return currentEducation;
    }

    public void setCurrentEducation(StringFilter currentEducation) {
        this.currentEducation = currentEducation;
    }

    public CandidateStatusFilter getStatus() {
        return status;
    }

    public Optional<CandidateStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public CandidateStatusFilter status() {
        if (status == null) {
            setStatus(new CandidateStatusFilter());
        }
        return status;
    }

    public void setStatus(CandidateStatusFilter status) {
        this.status = status;
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
        final CandidatCriteria that = (CandidatCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(nationality, that.nationality) &&
            Objects.equals(birthPlace, that.birthPlace) &&
            Objects.equals(idNumber, that.idNumber) &&
            Objects.equals(address, that.address) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(educationLevel, that.educationLevel) &&
            Objects.equals(school, that.school) &&
            Objects.equals(registrationNumber, that.registrationNumber) &&
            Objects.equals(currentEducation, that.currentEducation) &&
            Objects.equals(status, that.status) &&
            Objects.equals(managerId, that.managerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            firstName,
            lastName,
            birthDate,
            nationality,
            birthPlace,
            idNumber,
            address,
            email,
            phone,
            educationLevel,
            school,
            registrationNumber,
            currentEducation,
            status,
            managerId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CandidatCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFirstName().map(f -> "firstName=" + f + ", ").orElse("") +
            optionalLastName().map(f -> "lastName=" + f + ", ").orElse("") +
            optionalBirthDate().map(f -> "birthDate=" + f + ", ").orElse("") +
            optionalNationality().map(f -> "nationality=" + f + ", ").orElse("") +
            optionalBirthPlace().map(f -> "birthPlace=" + f + ", ").orElse("") +
            optionalIdNumber().map(f -> "idNumber=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalEducationLevel().map(f -> "educationLevel=" + f + ", ").orElse("") +
            optionalSchool().map(f -> "school=" + f + ", ").orElse("") +
            optionalRegistrationNumber().map(f -> "registrationNumber=" + f + ", ").orElse("") +
            optionalCurrentEducation().map(f -> "currentEducation=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalManagerId().map(f -> "managerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
