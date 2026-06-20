package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.TypeCanal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "app.sms.delivery-mode", havingValue = "http")
public class HttpSmsNotificationDeliveryGateway implements NotificationDeliveryGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSmsNotificationDeliveryGateway.class);

    private final String provider;
    private final String sendPath;
    private final ProviderEndpoint wave;
    private final ProviderEndpoint orangeMoney;
    private final ProviderEndpoint mtnMomo;

    public HttpSmsNotificationDeliveryGateway(
            @Value("${app.sms.provider}") String provider,
            @Value("${app.sms.send-path}") String sendPath,
            @Value("${soutra.integration.wave.api-url}") String waveApiUrl,
            @Value("${soutra.integration.wave.api-key}") String waveApiKey,
            @Value("${soutra.integration.orange-money.api-url}") String orangeApiUrl,
            @Value("${soutra.integration.orange-money.api-key}") String orangeApiKey,
            @Value("${soutra.integration.mtn-momo.api-url}") String mtnApiUrl,
            @Value("${soutra.integration.mtn-momo.api-key}") String mtnApiKey
    ) {
        this.provider = provider;
        this.sendPath = sendPath;
        this.wave = new ProviderEndpoint(waveApiUrl, waveApiKey);
        this.orangeMoney = new ProviderEndpoint(orangeApiUrl, orangeApiKey);
        this.mtnMomo = new ProviderEndpoint(mtnApiUrl, mtnApiKey);
    }

    @Override
    public NotificationDeliveryResult envoyer(Client client, TypeCanal canal, String message) {
        if (client.getTelephone() == null || client.getTelephone().isBlank()) {
            return NotificationDeliveryResult.echec("Aucun numero de telephone n'est renseigne pour ce client");
        }
        if (message == null || message.isBlank()) {
            return NotificationDeliveryResult.echec("Le message de notification est vide");
        }

        ProviderEndpoint endpoint = resolveEndpoint();
        if (endpoint.apiKey() == null || endpoint.apiKey().isBlank()) {
            return NotificationDeliveryResult.echec("Cle API SMS absente pour le provider " + provider);
        }

        try {
            RestClient.builder()
                    .baseUrl(endpoint.apiUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + endpoint.apiKey())
                    .build()
                    .post()
                    .uri(sendPath)
                    .body(new SmsPayload(client.getTelephone(), message, client.getCodeClient(), canal.getCodeCanal()))
                    .retrieve()
                    .toBodilessEntity();
            return NotificationDeliveryResult.succes();
        } catch (RuntimeException ex) {
            LOGGER.warn("Echec SMS {} pour le client {}: {}", provider, client.getCodeClient(), ex.getMessage());
            return NotificationDeliveryResult.echec("Echec gateway SMS " + provider);
        }
    }

    private ProviderEndpoint resolveEndpoint() {
        return switch (provider.trim().toLowerCase()) {
            case "orange", "orange-money" -> orangeMoney;
            case "mtn", "mtn-momo" -> mtnMomo;
            case "wave" -> wave;
            default -> throw new IllegalArgumentException("Provider SMS non supporte: " + provider);
        };
    }

    private record ProviderEndpoint(String apiUrl, String apiKey) {
    }

    private record SmsPayload(String telephone, String message, String codeClient, String canal) {
    }
}
