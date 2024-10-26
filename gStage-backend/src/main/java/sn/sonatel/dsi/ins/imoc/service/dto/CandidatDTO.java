package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.CandidateStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.EducationLevel;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.Candidat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CandidatDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2)
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(min = 2)
    private String lastName;

    @NotNull(message = "must not be null")
    private LocalDate birthDate;

    @NotNull(message = "must not be null")
    private String nationality;

    @NotNull(message = "must not be null")
    private String birthPlace;

    @NotNull(message = "must not be null")
    private String idNumber;

    @NotNull(message = "must not be null")
    private String address;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[0-9]{9}$")
    private String phone;

    @NotNull(message = "must not be null")
    private EducationLevel educationLevel;

    @NotNull(message = "must not be null")
    private String school;

    private String registrationNumber;

    @NotNull(message = "must not be null")
    private String currentEducation;

    @NotNull(message = "must not be null")
    private CandidateStatus status;

    private ManagerDTO manager;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCurrentEducation() {
        return currentEducation;
    }

    public void setCurrentEducation(String currentEducation) {
        this.currentEducation = currentEducation;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public ManagerDTO getManager() {
        return manager;
    }

    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CandidatDTO)) {
            return false;
        }

        CandidatDTO candidatDTO = (CandidatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, candidatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CandidatDTO{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", nationality='" + getNationality() + "'" +
            ", birthPlace='" + getBirthPlace() + "'" +
            ", idNumber='" + getIdNumber() + "'" +
            ", address='" + getAddress() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", educationLevel='" + getEducationLevel() + "'" +
            ", school='" + getSchool() + "'" +
            ", registrationNumber='" + getRegistrationNumber() + "'" +
            ", currentEducation='" + getCurrentEducation() + "'" +
            ", status='" + getStatus() + "'" +
            ", manager=" + getManager() +
            "}";
    }
}
