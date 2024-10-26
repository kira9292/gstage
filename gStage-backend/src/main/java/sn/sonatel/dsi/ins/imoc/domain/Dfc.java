package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Dfc.
 */
@Table("dfc")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dfc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    @JsonIgnoreProperties(value = { "attestationPresences", "contrat", "dfc", "assistantGWTECreator" }, allowSetters = true)
    private Set<EtatPaiement> etatPaiements = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dfc id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<EtatPaiement> getEtatPaiements() {
        return this.etatPaiements;
    }

    public void setEtatPaiements(Set<EtatPaiement> etatPaiements) {
        if (this.etatPaiements != null) {
            this.etatPaiements.forEach(i -> i.setDfc(null));
        }
        if (etatPaiements != null) {
            etatPaiements.forEach(i -> i.setDfc(this));
        }
        this.etatPaiements = etatPaiements;
    }

    public Dfc etatPaiements(Set<EtatPaiement> etatPaiements) {
        this.setEtatPaiements(etatPaiements);
        return this;
    }

    public Dfc addEtatPaiements(EtatPaiement etatPaiement) {
        this.etatPaiements.add(etatPaiement);
        etatPaiement.setDfc(this);
        return this;
    }

    public Dfc removeEtatPaiements(EtatPaiement etatPaiement) {
        this.etatPaiements.remove(etatPaiement);
        etatPaiement.setDfc(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dfc)) {
            return false;
        }
        return getId() != null && getId().equals(((Dfc) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dfc{" +
            "id=" + getId() +
            "}";
    }
}
