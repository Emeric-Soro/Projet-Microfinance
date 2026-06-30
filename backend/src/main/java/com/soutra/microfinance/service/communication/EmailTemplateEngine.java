package com.soutra.microfinance.service.communication;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Moteur de rendu des templates HTML d'emails via Thymeleaf.
 */
@Service
public class EmailTemplateEngine {

    private final TemplateEngine templateEngine;

    public EmailTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Rend le template HTML spécifié sous forme de chaîne de caractères.
     *
     * @param templateName nom du template (ex: "welcome", "reset-password") sans l'extension .html
     * @param variables    les variables à injecter dans le template
     * @return le code HTML rendu
     */
    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }
        return templateEngine.process("email/" + templateName, context);
    }
}
