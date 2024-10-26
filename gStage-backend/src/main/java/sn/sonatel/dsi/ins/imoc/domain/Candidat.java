package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.CandidateStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.EducationLevel;

/**
 * A Candidat.
 */
@Table("candidat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Candidat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2)
    @Column("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(min = 2)
    @Column("last_name")
    private String lastName;

    @NotNull(message = "must not be null")
    @Column("birth_date")
    private LocalDate birthDate;

    @NotNull(message = "must not be null")
    @Column("nationality")
    private String nationality;

    @NotNull(message = "must not be null")
    @Column("birth_place")
    private String birthPlace;

    @NotNull(message = "must not be null")
    @Column("id_number")
    private String idNumber;

    @NotNull(message = "must not be null")
    @Column("address")
    private String address;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[0-9]{9}$")
    @Column("phone")
    private String phone;

    @NotNull(message = "must not be null")
    @Column("education_level")
    private EducationLevel educationLevel;

    @NotNull(message = "must not be null")
    @Column("school")
    private String school;

    @Column("registration_number")
    private String registrationNumber;

    @NotNull(message = "must not be null")
    @Column("current_education")
    private String currentEducation;

    @NotNull(message = "must not be null")
    @Column("status")
    private CandidateStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "drh", "assistantGWTECreator", "candidat" }, allowSetters = true)
    private Set<Contrat> contrats = new HashSet<>();

    @Transient
    private DemandeStage demandeStage;

    @Transient
    @JsonIgnoreProperties(value = { "service", "attestationPresences", "candidats", "demandeStages" }, allowSetters = true)
    private Manager manager;

    @Column("manager_id")
    private Long managerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Candidat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Candidat firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Candidat lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public Candidat birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return this.nationality;
    }

    public Candidat nationality(String nationality) {
        this.setNationality(nationality);
        return this;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthPlace() {
        return this.birthPlace;
    }

    public Candidat birthPlace(String birthPlace) {
        this.setBirthPlace(birthPlace);
        return this;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public Candidat idNumber(String idNumber) {
        this.setIdNumber(idNumber);
        return this;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public Candidat address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public Candidat email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Candidat phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EducationLevel getEducationLevel() {
        return this.educationLevel;
    }

    public Candidat educationLevel(EducationLevel educationLevel) {
        this.setEducationLevel(educationLevel);
        return this;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getSchool() {
        return this.school;
    }

    public Candidat school(String school) {
        this.setSchool(school);
        return this;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public Candidat registrationNumber(String registrationNumber) {
        this.setRegistrationNumber(registrationNumber);
        return this;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCurrentEducation() {
        return this.currentEducation;
    }

    public Candidat currentEducation(String currentEducation) {
        this.setCurrentEducation(currentEducation);
        return this;
    }

    public void setCurrentEducation(String currentEducation) {
        this.currentEducation = currentEducation;
    }

    public CandidateStatus getStatus() {
        return this.status;
    }

    public Candidat status(CandidateStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public Set<Contrat> getContrats() {
        return this.contrats;
    }

    public void setContrats(Set<Contrat> contrats) {
        if (this.contrats != null) {
            this.contrats.forEach(i -> i.setCandidat(null));
        }
        if (contrats != null) {
            contrats.forEach(i -> i.setCandidat(this));
        }
        this.contrats = contrats;
    }

    public Candidat contrats(Set<Contrat> contrats) {
        this.setContrats(contrats);
        return this;
    }

    public Candidat addContrats(Contrat contrat) {
        this.contrats.add(contrat);
        contrat.setCandidat(this);
        return this;
    }

    public Candidat removeContrats(Contrat contrat) {
        this.contrats.remove(contrat);
        contrat.setCandidat(null);
        return this;
    }

    public DemandeStage getDemandeStage() {
        return this.demandeStage;
    }

    public void setDemandeStage(DemandeStage demandeStage) {
        if (this.demandeStage != null) {
            this.demandeStage.setCandidat(null);
        }
        if (demandeStage != null) {
            demandeStage.setCandidat(this);
        }
        this.demandeStage = demandeStage;
    }

    public Candidat demandeStage(DemandeStage demandeStage) {
        this.setDemandeStage(demandeStage);
        return this;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
        this.managerId = manager != null ? manager.getId() : null;
    }

    public Candidat manager(Manager manager) {
        this.setManager(manager);
        return this;
    }

    public Long getManagerId() {
        return this.managerId;
    }

    public void setManagerId(Long manager) {
        this.managerId = manager;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Candidat)) {
            return false;
        }
        return getId() != null && getId().equals(((Candidat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Candidat{" +
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
            "}";
    }
}
