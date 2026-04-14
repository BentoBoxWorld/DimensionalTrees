package world.bentobox.dimensionaltrees;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MaterialWeightedPicker}.
 */
class MaterialWeightedPickerTest {

    @Test
    void testPickRandomNullWeightsReturnsAir() {
        assertEquals("air", MaterialWeightedPicker.pickRandom(null));
    }

    @Test
    void testPickRandomEmptyWeightsReturnsAir() {
        assertEquals("air", MaterialWeightedPicker.pickRandom(new HashMap<>()));
    }

    @Test
    void testPickRandomAllZeroWeightsReturnsAir() {
        Map<String, Integer> weights = new LinkedHashMap<>();
        weights.put("gravel", 0);
        weights.put("netherrack", 0);
        assertEquals("air", MaterialWeightedPicker.pickRandom(weights));
    }

    @Test
    void testPickRandomSingleMaterial100Percent() {
        // With 100% weight, the material must always be picked.
        Map<String, Integer> weights = Map.of("gravel", 100);
        for (int i = 0; i < 50; i++) {
            assertEquals("gravel", MaterialWeightedPicker.pickRandom(weights));
        }
    }

    @Test
    void testPickRandomSingleMaterialLessThan100CanPickAir() {
        // 50% gravel → across many rolls some must be "air"
        Map<String, Integer> weights = Map.of("gravel", 50);
        boolean sawGravel = false;
        boolean sawAir = false;
        for (int i = 0; i < 1000; i++) {
            String picked = MaterialWeightedPicker.pickRandom(weights);
            if ("gravel".equals(picked)) sawGravel = true;
            if ("air".equals(picked)) sawAir = true;
        }
        assertTrue(sawGravel, "Expected gravel to be picked at least once");
        assertTrue(sawAir, "Expected air to be picked at least once when weight < 100");
    }

    @Test
    void testPickRandomWeightsOver100ScalesProportionally() {
        // gravel:80, netherrack:40 → total 120, no AIR expected
        Map<String, Integer> weights = new LinkedHashMap<>();
        weights.put("gravel", 80);
        weights.put("netherrack", 40);
        boolean sawGravel = false;
        boolean sawNetherrack = false;
        boolean sawAir = false;
        for (int i = 0; i < 1000; i++) {
            String picked = MaterialWeightedPicker.pickRandom(weights);
            if ("gravel".equals(picked)) sawGravel = true;
            else if ("netherrack".equals(picked)) sawNetherrack = true;
            else sawAir = true;
        }
        assertTrue(sawGravel, "Expected gravel to be picked");
        assertTrue(sawNetherrack, "Expected netherrack to be picked");
        org.junit.jupiter.api.Assertions.assertFalse(sawAir,
                "Expected no AIR when total weight > 100 (proportional scaling)");
    }

    @Test
    void testPickRandomExactly100NoAir() {
        // gravel:80, netherrack:20 → total 100, no AIR expected
        Map<String, Integer> weights = new LinkedHashMap<>();
        weights.put("gravel", 80);
        weights.put("netherrack", 20);
        for (int i = 0; i < 500; i++) {
            String picked = MaterialWeightedPicker.pickRandom(weights);
            assertTrue("gravel".equals(picked) || "netherrack".equals(picked),
                    "Only gravel or netherrack expected, got: " + picked);
        }
    }

    @Test
    void testPickRandomZeroWeightEntriesIgnored() {
        // Only gravel has positive weight; obsidian:0 should never be picked
        Map<String, Integer> weights = new LinkedHashMap<>();
        weights.put("obsidian", 0);
        weights.put("gravel", 100);
        for (int i = 0; i < 50; i++) {
            assertEquals("gravel", MaterialWeightedPicker.pickRandom(weights));
        }
    }

    @Test
    void testPickRandomLowercasesResult() {
        Map<String, Integer> weights = Map.of("GRAVEL", 100);
        assertEquals("gravel", MaterialWeightedPicker.pickRandom(weights));
    }

    @Test
    void testPickRandomApproximateProbabilities() {
        // gravel:70, netherrack:30 → ~70% gravel across many rolls
        Map<String, Integer> weights = new LinkedHashMap<>();
        weights.put("gravel", 70);
        weights.put("netherrack", 30);
        int gravel = 0;
        int total = 10000;
        for (int i = 0; i < total; i++) {
            if ("gravel".equals(MaterialWeightedPicker.pickRandom(weights))) gravel++;
        }
        // Allow ±5% tolerance
        double ratio = (double) gravel / total;
        assertTrue(ratio > 0.65 && ratio < 0.75,
                "Expected ~70% gravel probability, got " + (ratio * 100) + "%");
    }
}

