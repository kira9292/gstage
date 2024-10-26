package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurationStagiaireDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    private LocalDate endDate;

    @NotNull(message = "must not be null")
    private Boolean status;

    private String cardNumber;

    private CandidatDTO candidat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CandidatDTO getCandidat() {
        return candidat;
    }

    public void setCandidat(CandidatDTO candidat) {
        this.candidat = candidat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurationStagiaireDTO)) {
            return false;
        }

        RestaurationStagiaireDTO restaurationStagiaireDTO = (RestaurationStagiaireDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurationStagiaireDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurationStagiaireDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", cardNumber='" + getCardNumber() + "'" +
            ", candidat=" + getCandidat() +
            "}";
    }
}
