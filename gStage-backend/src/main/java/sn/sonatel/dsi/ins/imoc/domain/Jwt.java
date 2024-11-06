package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A Jwt.
 */
@Entity
@Table(name = "jwt")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Jwt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "desactive")
    private Boolean desactive;

    @Column(name = "expire")
    private Boolean expire;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Jwt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDesactive() {
        return this.desactive;
    }

    public Jwt desactive(Boolean desactive) {
        this.setDesactive(desactive);
        return this;
    }

    public void setDesactive(Boolean desactive) {
        this.desactive = desactive;
    }

    public Boolean getExpire() {
        return this.expire;
    }

    public Jwt expire(Boolean expire) {
        this.setExpire(expire);
        return this;
    }

    public void setExpire(Boolean expire) {
        this.expire = expire;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Jwt appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Jwt)) {
            return false;
        }
        return getId() != null && getId().equals(((Jwt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Jwt{" +
            "id=" + getId() +
            ", desactive='" + getDesactive() + "'" +
            ", expire='" + getExpire() + "'" +
            "}";
    }
}
