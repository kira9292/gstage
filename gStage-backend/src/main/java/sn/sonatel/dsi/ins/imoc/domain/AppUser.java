package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A AppUser.
 */
@Entity
@Table(name = "app_user")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUser implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Size(min = 8)
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "statue")
    private Boolean statue;

    @JsonIgnoreProperties(value = { "businessUnit", "appUser", "departemen" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Service service;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonIgnoreProperties(value = { "attestationPresences", "contrat", "appUser" }, allowSetters = true)
    private Set<EtatPaiement> etatPaiements = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "appUser", "candidat" }, allowSetters = true)
    private Set<Contrat> contrats = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonIgnoreProperties(value = { "candidat", "appUser", "departement", "businessUnit" }, allowSetters = true)
    private Set<DemandeStage> demandeStages = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonIgnoreProperties(value = { "validations", "contrat", "appUser", "etatPaiement" }, allowSetters = true)
    private Set<AttestationPresence> attestationPresences = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonIgnoreProperties(value = { "contrats", "demandeStage", "appUser" }, allowSetters = true)
    private Set<Candidat> candidats = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnoreProperties(value = { "attestationPresence", "contrat", "attestationFinStage", "user" }, allowSetters = true)
    private Set<Validation> validations = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_app_user__role",
        joinColumns = @JoinColumn(name = "app_user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnoreProperties(value = { "appUsers" }, allowSetters = true)
    private Set<Role> roles = new HashSet<>();

    @JsonIgnoreProperties(value = { "appUser" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "appUser")
    private ValidationStatusUser validationStatusUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AppUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public AppUser username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public AppUser email(String email) {
        this.setEmail(email);
        return this;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        for (Role role : this.roles) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_"+role.getName());
            list.add(simpleGrantedAuthority);
        }
        return list;
    }

    public String getPassword() {
        return this.password;
    }

    public AppUser password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public AppUser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public AppUser firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Boolean getStatue() {
        return this.statue;
    }

    public AppUser statue(Boolean statue) {
        this.setStatue(statue);
        return this;
    }

    public void setStatue(Boolean statue) {
        this.statue = statue;
    }

    public Service getService() {
        return this.service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public AppUser service(Service service) {
        this.setService(service);
        return this;
    }

    public Set<EtatPaiement> getEtatPaiements() {
        return this.etatPaiements;
    }

    public void setEtatPaiements(Set<EtatPaiement> etatPaiements) {
        if (this.etatPaiements != null) {
            this.etatPaiements.forEach(i -> i.setAppUser(null));
        }
        if (etatPaiements != null) {
            etatPaiements.forEach(i -> i.setAppUser(this));
        }
        this.etatPaiements = etatPaiements;
    }

    public AppUser etatPaiements(Set<EtatPaiement> etatPaiements) {
        this.setEtatPaiements(etatPaiements);
        return this;
    }

    public AppUser addEtatPaiement(EtatPaiement etatPaiement) {
        this.etatPaiements.add(etatPaiement);
        etatPaiement.setAppUser(this);
        return this;
    }

    public AppUser removeEtatPaiement(EtatPaiement etatPaiement) {
        this.etatPaiements.remove(etatPaiement);
        etatPaiement.setAppUser(null);
        return this;
    }

    public Set<Contrat> getContrats() {
        return this.contrats;
    }

    public void setContrats(Set<Contrat> contrats) {
        if (this.contrats != null) {
            this.contrats.forEach(i -> i.setAppUser(null));
        }
        if (contrats != null) {
            contrats.forEach(i -> i.setAppUser(this));
        }
        this.contrats = contrats;
    }

    public AppUser contrats(Set<Contrat> contrats) {
        this.setContrats(contrats);
        return this;
    }

    public AppUser addContrat(Contrat contrat) {
        this.contrats.add(contrat);
        contrat.setAppUser(this);
        return this;
    }

    public AppUser removeContrat(Contrat contrat) {
        this.contrats.remove(contrat);
        contrat.setAppUser(null);
        return this;
    }

    public Set<DemandeStage> getDemandeStages() {
        return this.demandeStages;
    }

    public void setDemandeStages(Set<DemandeStage> demandeStages) {
        if (this.demandeStages != null) {
            this.demandeStages.forEach(i -> i.setAppUser(null));
        }
        if (demandeStages != null) {
            demandeStages.forEach(i -> i.setAppUser(this));
        }
        this.demandeStages = demandeStages;
    }

    public AppUser demandeStages(Set<DemandeStage> demandeStages) {
        this.setDemandeStages(demandeStages);
        return this;
    }

    public AppUser addDemandeStage(DemandeStage demandeStage) {
        this.demandeStages.add(demandeStage);
        demandeStage.setAppUser(this);
        return this;
    }

    public AppUser removeDemandeStage(DemandeStage demandeStage) {
        this.demandeStages.remove(demandeStage);
        demandeStage.setAppUser(null);
        return this;
    }

    public Set<AttestationPresence> getAttestationPresences() {
        return this.attestationPresences;
    }

    public void setAttestationPresences(Set<AttestationPresence> attestationPresences) {
        if (this.attestationPresences != null) {
            this.attestationPresences.forEach(i -> i.setAppUser(null));
        }
        if (attestationPresences != null) {
            attestationPresences.forEach(i -> i.setAppUser(this));
        }
        this.attestationPresences = attestationPresences;
    }

    public AppUser attestationPresences(Set<AttestationPresence> attestationPresences) {
        this.setAttestationPresences(attestationPresences);
        return this;
    }

    public AppUser addAttestationPresence(AttestationPresence attestationPresence) {
        this.attestationPresences.add(attestationPresence);
        attestationPresence.setAppUser(this);
        return this;
    }

    public AppUser removeAttestationPresence(AttestationPresence attestationPresence) {
        this.attestationPresences.remove(attestationPresence);
        attestationPresence.setAppUser(null);
        return this;
    }

    public Set<Candidat> getCandidats() {
        return this.candidats;
    }

    public void setCandidats(Set<Candidat> candidats) {
        if (this.candidats != null) {
            this.candidats.forEach(i -> i.setAppUser(null));
        }
        if (candidats != null) {
            candidats.forEach(i -> i.setAppUser(this));
        }
        this.candidats = candidats;
    }

    public AppUser candidats(Set<Candidat> candidats) {
        this.setCandidats(candidats);
        return this;
    }

    public AppUser addCandidat(Candidat candidat) {
        this.candidats.add(candidat);
        candidat.setAppUser(this);
        return this;
    }

    public AppUser removeCandidat(Candidat candidat) {
        this.candidats.remove(candidat);
        candidat.setAppUser(null);
        return this;
    }

    public Set<Validation> getValidations() {
        return this.validations;
    }

    public void setValidations(Set<Validation> validations) {
        if (this.validations != null) {
            this.validations.forEach(i -> i.setUser(null));
        }
        if (validations != null) {
            validations.forEach(i -> i.setUser(this));
        }
        this.validations = validations;
    }

    public AppUser validations(Set<Validation> validations) {
        this.setValidations(validations);
        return this;
    }

    public AppUser addValidations(Validation validation) {
        this.validations.add(validation);
        validation.setUser(this);
        return this;
    }

    public AppUser removeValidations(Validation validation) {
        this.validations.remove(validation);
        validation.setUser(null);
        return this;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public AppUser roles(Set<Role> roles) {
        this.setRoles(roles);
        return this;
    }

    public AppUser addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public AppUser removeRole(Role role) {
        this.roles.remove(role);
        return this;
    }

    public ValidationStatusUser getValidationStatusUser() {
        return this.validationStatusUser;
    }

    public void setValidationStatusUser(ValidationStatusUser validationStatusUser) {
        if (this.validationStatusUser != null) {
            this.validationStatusUser.setAppUser(null);
        }
        if (validationStatusUser != null) {
            validationStatusUser.setAppUser(this);
        }
        this.validationStatusUser = validationStatusUser;
    }

    public AppUser validationStatusUser(ValidationStatusUser validationStatusUser) {
        this.setValidationStatusUser(validationStatusUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return getId() != null && getId().equals(((AppUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", name='" + getName() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", statue='" + getStatue() + "'" +
            "}";
    }




}
