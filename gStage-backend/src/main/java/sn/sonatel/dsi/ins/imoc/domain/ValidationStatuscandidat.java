package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ValidationStatuscandidat.
 */
@Entity
@Table(name = "validation_statuscandidat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ValidationStatuscandidat implements Serializable {

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

    @JsonIgnoreProperties(value = { "demandeStage", "validationStatuscandidat", "appUser" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Candidat candidat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ValidationStatuscandidat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreation() {
        return this.creation;
    }

    public ValidationStatuscandidat creation(Instant creation) {
        this.setCreation(creation);
        return this;
    }

    public void setCreation(Instant creation) {
        this.creation = creation;
    }

    public Instant getExpire() {
        return this.expire;
    }

    public ValidationStatuscandidat expire(Instant expire) {
        this.setExpire(expire);
        return this;
    }

    public void setExpire(Instant expire) {
        this.expire = expire;
    }

    public Instant getActivation() {
        return this.activation;
    }

    public ValidationStatuscandidat activation(Instant activation) {
        this.setActivation(activation);
        return this;
    }

    public void setActivation(Instant activation) {
        this.activation = activation;
    }

    public String getCode() {
        return this.code;
    }

    public ValidationStatuscandidat code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Candidat getCandidat() {
        return this.candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public ValidationStatuscandidat candidat(Candidat candidat) {
        this.setCandidat(candidat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValidationStatuscandidat)) {
            return false;
        }
        return getId() != null && getId().equals(((ValidationStatuscandidat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ValidationStatuscandidat{" +
            "id=" + getId() +
            ", creation='" + getCreation() + "'" +
            ", expire='" + getExpire() + "'" +
            ", activation='" + getActivation() + "'" +
            ", code='" + getCode() + "'" +
            "}";
    }
}
