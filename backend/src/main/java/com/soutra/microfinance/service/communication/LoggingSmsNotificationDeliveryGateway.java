package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.TypeCanal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingSmsNotificationDeliveryGateway implements NotificationDeliveryGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingSmsNotificationDeliveryGateway.class);

    @Override
    public NotificationDeliveryResult envoyer(Client client, TypeCanal canal, String message) {
        if (client.getTelephone() == null || client.getTelephone().isBlank()) {
            return NotificationDeliveryResult.echec("Aucun numero de telephone n'est renseigne pour ce client");
        }
        if (message == null || message.isBlank()) {
            return NotificationDeliveryResult.echec("Le message de notification est vide");
        }

        LOGGER.info(
                "Notification {} envoyee au client {} sur {} ({})",
                canal.getCodeCanal(),
                client.getCodeClient(),
                client.getTelephone(),
                message
        );

        return NotificationDeliveryResult.succes();
    }
}
