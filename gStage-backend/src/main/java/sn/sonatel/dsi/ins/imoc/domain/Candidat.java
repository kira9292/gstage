package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.CandidateStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.EducationLevel;

/**
 * A Candidat.
 */
@Entity
@Table(name = "candidat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Candidat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 2)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotNull
    @Column(name = "nationality", nullable = false)
    private String nationality;

    @NotNull
    @Column(name = "birth_place", nullable = false)
    private String birthPlace;

    @NotNull
    @Column(name = "id_number", nullable = false, unique = true)
    private String idNumber;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Pattern(regexp = "^[0-9]{9}$")
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", nullable = false)
    private EducationLevel educationLevel;

    @NotNull
    @Column(name = "school", nullable = false)
    private String school;

    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    @NotNull
    @Column(name = "current_education", nullable = false)
    private String currentEducation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CandidateStatus status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "candidat")
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "appUser", "candidat" }, allowSetters = true)
    private Set<Contrat> contrats = new HashSet<>();

    @JsonIgnoreProperties(value = { "candidat", "appUser", "departement", "businessUnit" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "candidat")
    private DemandeStage demandeStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "service", "etatPaiements", "contrats", "demandeStages", "attestationPresences", "candidats", "validations", "roles" },
        allowSetters = true
    )
    private AppUser appUser;

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

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Candidat appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
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
