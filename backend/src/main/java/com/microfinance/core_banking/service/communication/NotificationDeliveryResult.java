package com.microfinance.core_banking.service.communication;

public record NotificationDeliveryResult(
        boolean reussi,
        String erreur
) {

    public static NotificationDeliveryResult succes() {
        return new NotificationDeliveryResult(true, null);
    }

    public static NotificationDeliveryResult echec(String erreur) {
        return new NotificationDeliveryResult(false, erreur);
    }
}
