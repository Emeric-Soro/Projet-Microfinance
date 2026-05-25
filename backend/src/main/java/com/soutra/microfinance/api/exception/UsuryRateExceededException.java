package com.soutra.microfinance.api.exception;

// Exception levee lorsque le TAEG calcule depasse le plafond d'usure BCEAO (24%).
public class UsuryRateExceededException extends RuntimeException {

    private final double taegCalcule;
    private final double plafondUsure;

    public UsuryRateExceededException(double taegCalcule, double plafondUsure) {
        super(String.format(
                "Le TAEG calcule (%.2f%%) depasse le plafond d'usure BCEAO de %.2f%%. " +
                "Le credit ne peut etre accorde conformement a la reglementation UEMOA.",
                taegCalcule * 100, plafondUsure * 100
        ));
        this.taegCalcule = taegCalcule;
        this.plafondUsure = plafondUsure;
    }

    public double getTaegCalcule() {
        return taegCalcule;
    }

    public double getPlafondUsure() {
        return plafondUsure;
    }
}
