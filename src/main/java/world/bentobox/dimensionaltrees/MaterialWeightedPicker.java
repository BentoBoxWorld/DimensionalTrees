package world.bentobox.dimensionaltrees;

import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Utility for weighted random material selection.
 *
 * <p>Given a map of material name → weight (percentage), picks a random material
 * according to the configured weights.</p>
 *
 * <ul>
 *   <li>If weights sum to exactly 100 each material appears at its stated percentage.</li>
 *   <li>If weights sum to more than 100 the weights are scaled proportionally so that no
 *       AIR is injected.</li>
 *   <li>If weights sum to less than 100 the remaining percentage is filled with
 *       {@code "air"} (i.e. the block is left as-is or replaced to air).</li>
 * </ul>
 */
public class MaterialWeightedPicker {

    private static final Random RANDOM = new Random();

    private MaterialWeightedPicker() {
        // utility class
    }

    /**
     * Picks a random material from a weighted map.
     *
     * @param weights map of material name (lowercase) to weight percentage; may be null or empty
     * @return the chosen material name in lowercase, or {@code "air"} if {@code weights} is
     *         null/empty or if the roll lands in the unfilled portion
     */
    public static String pickRandom(Map<String, Integer> weights) {
        if (weights == null || weights.isEmpty()) {
            return "air";
        }
        int total = weights.values().stream().mapToInt(i -> Math.max(i, 0)).sum();
        if (total <= 0) {
            return "air";
        }
        // Pool size:
        //   total > 100 → use total as pool (proportional scaling, no AIR)
        //   total ≤ 100 → use 100 as pool (remaining 100-total chance is AIR)
        int pool = Math.max(total, 100);
        int roll = RANDOM.nextInt(pool);

        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            int w = entry.getValue();
            if (w <= 0) continue;
            cumulative += w;
            if (roll < cumulative) {
                return entry.getKey().toLowerCase(Locale.ENGLISH);
            }
        }
        // Roll fell in the AIR portion (only when total < 100)
        return "air";
    }
}
