package com.microfinance.core_banking.service.communication;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.TypeCanal;

public interface NotificationDeliveryGateway {

    NotificationDeliveryResult envoyer(Client client, TypeCanal canal, String message);
}
