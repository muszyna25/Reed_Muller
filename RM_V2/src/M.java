
/**
 * @author Steve-David Marguet
 *
 */
public class M {

    /**
     * Returns the exponent to which 2 must be raised to produce the value given
     * as parameter.
     * 
     * @param value
     *            The value form which we want the logarithm to base 2.
     * @return The result.
     */
    public static int log2(int value){
        return Integer.SIZE-Integer.numberOfLeadingZeros(value);
    }

    public static int hamming(Integer a, Integer b) {
        int distance = -1;
        if (a == null || b == null) {
            return distance;
        } else {
            distance = 0;
            Integer  diff = (a ^ b);
            for (int i = 0; i < Integer.SIZE; i++) {
                if ((diff & 1) == 1) {
                    distance++;
                }
                diff >>= 1;
            }
            return distance;
        }
    }
}
