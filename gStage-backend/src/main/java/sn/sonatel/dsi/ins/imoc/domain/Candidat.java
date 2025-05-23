package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.EducationLevel;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.Formation;

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

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "cni", unique = true)
    private String cni;

    @Column(name = "address")
    private String address;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Pattern(regexp = "^[0-9]{9}$")
    @Column(name = "phone", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level")
    private EducationLevel educationLevel;

    @Column(name = "school")
    private String school;

    @Enumerated(EnumType.STRING)
    @Column(name = "formation")
    private Formation formation;

    @JsonIgnoreProperties(value = { "validations", "appuser" }, allowSetters = true)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(unique = true)
    private AttestationFinStage attestationFinStage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "candidat")
    @JsonIgnoreProperties(value = { "candidat" }, allowSetters = true)
    private Set<EtatPaiement> etatPaiements = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "candidat")
    @JsonIgnoreProperties(value = { "validations", "candidat" }, allowSetters = true)
    private Set<Contrat> contrats = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "candidat")
    @JsonIgnoreProperties(value = { "validations", "candidat" }, allowSetters = true)
    private Set<AttestationPresence> attestationPresences = new HashSet<>();

    @JsonIgnoreProperties(value = { "candidat", "appUser", "departement", "businessUnit" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "candidat")
    private DemandeStage demandeStage;

    @JsonIgnoreProperties(value = { "candidat" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "candidat")
    private ValidationStatuscandidat validationStatuscandidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "service",
            "demandeStages",
            "candidats",
            "validations",
            "notifications",
            "role",
            "validationStatusUser",
            "restaurationStagiaires",
            "jwts",
        },
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

    public String getCni() {
        return this.cni;
    }

    public Candidat cni(String cni) {
        this.setCni(cni);
        return this;
    }

    public void setCni(String cni) {
        this.cni = cni;
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

    public Formation getFormation() {
        return this.formation;
    }

    public Candidat formation(Formation formation) {
        this.setFormation(formation);
        return this;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public AttestationFinStage getAttestationFinStage() {
        return this.attestationFinStage;
    }

    public void setAttestationFinStage(AttestationFinStage attestationFinStage) {
        this.attestationFinStage = attestationFinStage;
    }

    public Candidat attestationFinStage(AttestationFinStage attestationFinStage) {
        this.setAttestationFinStage(attestationFinStage);
        return this;
    }

    public Set<EtatPaiement> getEtatPaiements() {
        return this.etatPaiements;
    }

    public void setEtatPaiements(Set<EtatPaiement> etatPaiements) {
        if (this.etatPaiements != null) {
            this.etatPaiements.forEach(i -> i.setCandidat(null));
        }
        if (etatPaiements != null) {
            etatPaiements.forEach(i -> i.setCandidat(this));
        }
        this.etatPaiements = etatPaiements;
    }

    public Candidat etatPaiements(Set<EtatPaiement> etatPaiements) {
        this.setEtatPaiements(etatPaiements);
        return this;
    }

    public Candidat addEtatPaiement(EtatPaiement etatPaiement) {
        this.etatPaiements.add(etatPaiement);
        etatPaiement.setCandidat(this);
        return this;
    }

    public Candidat removeEtatPaiement(EtatPaiement etatPaiement) {
        this.etatPaiements.remove(etatPaiement);
        etatPaiement.setCandidat(null);
        return this;
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

    public Candidat addContrat(Contrat contrat) {
        this.contrats.add(contrat);
        contrat.setCandidat(this);
        return this;
    }

    public Candidat removeContrat(Contrat contrat) {
        this.contrats.remove(contrat);
        contrat.setCandidat(null);
        return this;
    }

    public Set<AttestationPresence> getAttestationPresences() {
        return this.attestationPresences;
    }

    public void setAttestationPresences(Set<AttestationPresence> attestationPresences) {
        if (this.attestationPresences != null) {
            this.attestationPresences.forEach(i -> i.setCandidat(null));
        }
        if (attestationPresences != null) {
            attestationPresences.forEach(i -> i.setCandidat(this));
        }
        this.attestationPresences = attestationPresences;
    }

    public Candidat attestationPresences(Set<AttestationPresence> attestationPresences) {
        this.setAttestationPresences(attestationPresences);
        return this;
    }

    public Candidat addAttestationPresence(AttestationPresence attestationPresence) {
        this.attestationPresences.add(attestationPresence);
        attestationPresence.setCandidat(this);
        return this;
    }

    public Candidat removeAttestationPresence(AttestationPresence attestationPresence) {
        this.attestationPresences.remove(attestationPresence);
        attestationPresence.setCandidat(null);
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

    public ValidationStatuscandidat getValidationStatuscandidat() {
        return this.validationStatuscandidat;
    }

    public void setValidationStatuscandidat(ValidationStatuscandidat validationStatuscandidat) {
        if (this.validationStatuscandidat != null) {
            this.validationStatuscandidat.setCandidat(null);
        }
        if (validationStatuscandidat != null) {
            validationStatuscandidat.setCandidat(this);
        }
        this.validationStatuscandidat = validationStatuscandidat;
    }

    public Candidat validationStatuscandidat(ValidationStatuscandidat validationStatuscandidat) {
        this.setValidationStatuscandidat(validationStatuscandidat);
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
            ", cni='" + getCni() + "'" +
            ", address='" + getAddress() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", educationLevel='" + getEducationLevel() + "'" +
            ", school='" + getSchool() + "'" +
            ", formation='" + getFormation() + "'" +
            "}";
    }
}
