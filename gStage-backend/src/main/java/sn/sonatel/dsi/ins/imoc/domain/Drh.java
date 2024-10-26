package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Drh.
 */
@Table("drh")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Drh implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Pattern(regexp = "^[0-9]{9}$")
    @Column("phone")
    private String phone;

    @Transient
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "drh", "assistantGWTECreator", "candidat" }, allowSetters = true)
    private Set<Contrat> contrats = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Drh id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public Drh phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Contrat> getContrats() {
        return this.contrats;
    }

    public void setContrats(Set<Contrat> contrats) {
        if (this.contrats != null) {
            this.contrats.forEach(i -> i.setDrh(null));
        }
        if (contrats != null) {
            contrats.forEach(i -> i.setDrh(this));
        }
        this.contrats = contrats;
    }

    public Drh contrats(Set<Contrat> contrats) {
        this.setContrats(contrats);
        return this;
    }

    public Drh addContrats(Contrat contrat) {
        this.contrats.add(contrat);
        contrat.setDrh(this);
        return this;
    }

    public Drh removeContrats(Contrat contrat) {
        this.contrats.remove(contrat);
        contrat.setDrh(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Drh)) {
            return false;
        }
        return getId() != null && getId().equals(((Drh) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Drh{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
