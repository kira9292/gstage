package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.AttestationPresence} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationPresenceDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String reference;

    @NotNull(message = "must not be null")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    private LocalDate endDate;

    @NotNull(message = "must not be null")
    private LocalDate signatureDate;

    @NotNull(message = "must not be null")
    private Boolean status;

    private String comments;

    private ContratDTO contrat;

    private ManagerDTO manager;

    private EtatPaiementDTO etatPaiement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ContratDTO getContrat() {
        return contrat;
    }

    public void setContrat(ContratDTO contrat) {
        this.contrat = contrat;
    }

    public ManagerDTO getManager() {
        return manager;
    }

    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    public EtatPaiementDTO getEtatPaiement() {
        return etatPaiement;
    }

    public void setEtatPaiement(EtatPaiementDTO etatPaiement) {
        this.etatPaiement = etatPaiement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttestationPresenceDTO)) {
            return false;
        }

        AttestationPresenceDTO attestationPresenceDTO = (AttestationPresenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, attestationPresenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttestationPresenceDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", signatureDate='" + getSignatureDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", comments='" + getComments() + "'" +
            ", contrat=" + getContrat() +
            ", manager=" + getManager() +
            ", etatPaiement=" + getEtatPaiement() +
            "}";
    }
}
