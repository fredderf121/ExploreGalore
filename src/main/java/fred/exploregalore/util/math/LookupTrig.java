package fred.exploregalore.util.math;

/**
 * Provides a lookup table and associated methods for sine and cosine (in degrees) at 90 degree intervals.
 * CURRENTLY UNUSED
 */
public final class LookupTrig {
    public static final double[] SINE_90_DEGREES_TABLE;

    static {
        SINE_90_DEGREES_TABLE = new double[]{0, 1, 0, -1};
    }

    /**
     * Returns sin(angle), with the assumption that the angle is a multiple of 90 degrees.
     * @param angle the angle in degrees
     * @return sin(angle)
     */
    public static double sine90Deg(double angle) {
        int numMultiplesOf90Deg = (int) (angle / 90);
        return SINE_90_DEGREES_TABLE[numMultiplesOf90Deg % 4];
    }

    /**
     * Returns cos(angle), with the assumption that the angle is a multiple of 90 degrees.
     * Uses the sin90Deg function, since sin(x) is identical to cos(x), but phase-shifted 90 degrees to the right.
     * @param angle the angle in degrees
     * @return cos(angle)
     */
    public static double cos90Deg(double angle) {
        return sine90Deg(angle + 90);
    }
}
