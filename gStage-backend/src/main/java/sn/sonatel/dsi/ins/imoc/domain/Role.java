package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;

/**
 * A Role.
 */
@Entity
@Table(name = "role")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private ERole name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    @JsonIgnoreProperties(
        value = {
            "service",
            "attestationFinStage",
            "etatPaiements",
            "contrats",
            "demandeStages",
            "attestationPresences",
            "candidats",
            "validations",
            "role",
            "validationStatusUser",
            "restaurationStagiaires",
            "jwts",
        },
        allowSetters = true
    )
    private Set<AppUser> appUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Role id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ERole getName() {
        return this.name;
    }

    public Role name(ERole name) {
        this.setName(name);
        return this;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    public Set<AppUser> getAppUsers() {
        return this.appUsers;
    }

    public void setAppUsers(Set<AppUser> appUsers) {
        if (this.appUsers != null) {
            this.appUsers.forEach(i -> i.setRole(null));
        }
        if (appUsers != null) {
            appUsers.forEach(i -> i.setRole(this));
        }
        this.appUsers = appUsers;
    }

    public Role appUsers(Set<AppUser> appUsers) {
        this.setAppUsers(appUsers);
        return this;
    }

    public Role addAppUser(AppUser appUser) {
        this.appUsers.add(appUser);
        appUser.setRole(this);
        return this;
    }

    public Role removeAppUser(AppUser appUser) {
        this.appUsers.remove(appUser);
        appUser.setRole(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        return getId() != null && getId().equals(((Role) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Role{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
