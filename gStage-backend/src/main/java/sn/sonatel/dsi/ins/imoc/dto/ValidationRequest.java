package sn.sonatel.dsi.ins.imoc.dto;

public class ValidationRequest {
    private Boolean validated;
    private String comments;

    // Getters et setters
    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
