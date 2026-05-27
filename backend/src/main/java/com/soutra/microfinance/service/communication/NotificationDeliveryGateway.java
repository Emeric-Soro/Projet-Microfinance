package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.TypeCanal;

public interface NotificationDeliveryGateway {

    NotificationDeliveryResult envoyer(Client client, TypeCanal canal, String message);
}
