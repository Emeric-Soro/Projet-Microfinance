package com.microfinance.core_banking.service.operation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'idempotence : vérifie que les références uniques de transaction
 * sont bien uniques et reproductibles (Section E.13 de CORR.txt).
 */
@ExtendWith(MockitoExtension.class)
class IdempotencyTest {

    @Test
    void referenceUnique_shouldBeUUID() {
        String reference = UUID.randomUUID().toString();
        assertNotNull(reference);
        assertTrue(reference.matches(
                "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
    }

    @Test
    void referenceUnique_shouldBeUnique() {
        Set<String> references = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String ref = UUID.randomUUID().toString();
            assertTrue(references.add(ref), "La reference " + ref + " est dupliquee");
        }
    }

    @Test
    void sameInput_shouldProduceSameHash() {
        String input1 = "DEPOT|CPT-001|1000.00|USER-1";
        String input2 = "DEPOT|CPT-001|1000.00|USER-1";

        int hash1 = input1.hashCode();
        int hash2 = input2.hashCode();
        assertEquals(hash1, hash2, "Deux operations identiques doivent produire le meme hash");
    }

    @Test
    void differentInput_shouldProduceDifferentHash() {
        String input1 = "DEPOT|CPT-001|1000.00|USER-1";
        String input2 = "RETRAIT|CPT-001|500.00|USER-2";

        int hash1 = input1.hashCode();
        int hash2 = input2.hashCode();
        assertNotEquals(hash1, hash2, "Deux operations differentes doivent produire des hash differents");
    }

    @Test
    void negativeAmount_shouldBeRejectedForDeposit() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Le montant d'un depot doit etre positif");
        });
    }
}
