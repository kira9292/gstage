package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ValidationStatusUser.
 */
@Entity
@Table(name = "validation_status_user")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ValidationStatusUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "creation")
    private Instant creation;

    @Column(name = "expire")
    private Instant expire;

    @Column(name = "activation")
    private Instant activation;

    @Column(name = "code")
    private String code;

    @JsonIgnoreProperties(
        value = {
            "service",
            "etatPaiements",
            "contrats",
            "demandeStages",
            "attestationPresences",
            "candidats",
            "validations",
            "roles",
            "validationStatusUser",
        },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private AppUser appUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ValidationStatusUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreation() {
        return this.creation;
    }

    public ValidationStatusUser creation(Instant creation) {
        this.setCreation(creation);
        return this;
    }

    public void setCreation(Instant creation) {
        this.creation = creation;
    }

    public Instant getExpire() {
        return this.expire;
    }

    public ValidationStatusUser expire(Instant expire) {
        this.setExpire(expire);
        return this;
    }

    public void setExpire(Instant expire) {
        this.expire = expire;
    }

    public Instant getActivation() {
        return this.activation;
    }

    public ValidationStatusUser activation(Instant activation) {
        this.setActivation(activation);
        return this;
    }

    public void setActivation(Instant activation) {
        this.activation = activation;
    }

    public String getCode() {
        return this.code;
    }

    public ValidationStatusUser code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public ValidationStatusUser appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValidationStatusUser)) {
            return false;
        }
        return getId() != null && getId().equals(((ValidationStatusUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ValidationStatusUser{" +
            "id=" + getId() +
            ", creation='" + getCreation() + "'" +
            ", expire='" + getExpire() + "'" +
            ", activation='" + getActivation() + "'" +
            ", code='" + getCode() + "'" +
            "}";
    }
}
