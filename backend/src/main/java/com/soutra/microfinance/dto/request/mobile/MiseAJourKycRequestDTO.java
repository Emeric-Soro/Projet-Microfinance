package com.soutra.microfinance.dto.request.mobile;

import java.math.BigDecimal;

public class MiseAJourKycRequestDTO {
    private String profession;
    private String secteurActivite;
    private BigDecimal revenuMensuel;

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSecteurActivite() {
        return secteurActivite;
    }

    public void setSecteurActivite(String secteurActivite) {
        this.secteurActivite = secteurActivite;
    }

    public BigDecimal getRevenuMensuel() {
        return revenuMensuel;
    }

    public void setRevenuMensuel(BigDecimal revenuMensuel) {
        this.revenuMensuel = revenuMensuel;
    }
}
