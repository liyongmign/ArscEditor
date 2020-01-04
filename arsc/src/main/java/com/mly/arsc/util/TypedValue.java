package com.mly.arsc.util;

public class TypedValue {
    /**
     * The value contains no data.
     */
    public static final int TYPE_NULL = 0x00;

    /**
     * The <var>data</var> field holds a resource identifier.
     */
    public static final int TYPE_REFERENCE = 0x01;
    /**
     * The <var>data</var> field holds an attribute resource
     * identifier (referencing an attribute in the current theme
     * style, not a resource entry).
     */
    public static final int TYPE_ATTRIBUTE = 0x02;
    /**
     * The <var>string</var> field holds string data.  In addition, if
     * <var>data</var> is non-zero then it is the string block
     * index of the string and <var>assetCookie</var> is the set of
     * assets the string came from.
     */
    public static final int TYPE_STRING = 0x03;
    /**
     * The <var>data</var> field holds an IEEE 754 floating point number.
     */
    public static final int TYPE_FLOAT = 0x04;
    /**
     * The <var>data</var> field holds a complex number encoding a
     * dimension value.
     */
    public static final int TYPE_DIMENSION = 0x05;
    /**
     * The <var>data</var> field holds a complex number encoding a fraction
     * of a container.
     */
    public static final int TYPE_FRACTION = 0x06;

    /**
     * Identifies the start of plain integer values.  Any type value
     * from this to {@link #TYPE_LAST_INT} means the
     * <var>data</var> field holds a generic integer value.
     */
    public static final int TYPE_FIRST_INT = 0x10;

    /**
     * The <var>data</var> field holds a number that was
     * originally specified in hexadecimal (0xn).
     */
    private static final int TYPE_INT_HEX = 0x11;
    /**
     * The <var>data</var> field holds 0 or 1 that was originally
     * specified as "false" or "true".
     */
    public static final int TYPE_INT_BOOLEAN = 0x12;

    /**
     * Identifies the start of integer values that were specified as
     * color constants (starting with '#').
     */
    public static final int TYPE_FIRST_COLOR_INT = 0x1c;

    /**
     * Identifies the end of integer values that were specified as color
     * constants.
     */
    public static final int TYPE_LAST_COLOR_INT = 0x1f;

    /**
     * Identifies the end of plain integer values.
     */
    public static final int TYPE_LAST_INT = 0x1f;

    /* ------------------------------------------------------------ */

    /**
     * Complex data: bit location of unit information.
     */
    private static final int COMPLEX_UNIT_SHIFT = 0;
    /**
     * Complex data: mask to extract unit information (after shifting by
     * {@link #COMPLEX_UNIT_SHIFT}). This gives us 16 possible types, as
     * defined below.
     */
    private static final int COMPLEX_UNIT_MASK = 0xf;

    /**
     * Complex data: where the radix information is, telling where the decimal
     * place appears in the mantissa.
     */
    private static final int COMPLEX_RADIX_SHIFT = 4;
    /**
     * Complex data: mask to extract radix information (after shifting by
     * {@link #COMPLEX_RADIX_SHIFT}). This give us 4 possible fixed point
     * representations as defined below.
     */
    private static final int COMPLEX_RADIX_MASK = 0x3;

    /**
     * Complex data: bit location of mantissa information.
     */
    private static final int COMPLEX_MANTISSA_SHIFT = 8;
    /**
     * Complex data: mask to extract mantissa information (after shifting by
     * {@link #COMPLEX_MANTISSA_SHIFT}). This gives us 23 bits of precision;
     * the top bit is the sign.
     */
    private static final int COMPLEX_MANTISSA_MASK = 0xffffff;

    /* ------------------------------------------------------------ */

    /* ------------------------------------------------------------ */

    private static final String[] DIMENSION_UNIT_STRS = new String[]{
            "px", "dip", "sp", "pt", "in", "mm"
    };
    private static final String[] FRACTION_UNIT_STRS = new String[]{
            "%", "%p"
    };

    /**
     * Perform type conversion on an
     * explicitly supplied type and data.
     *
     * @param type The data type identifier.
     * @param data The data value.
     * @return String The coerced string value.  If the value is
     * null or the type is not known, null is returned.
     */
    public static String coerceToString(int type, int data) {
        switch (type) {
            case TYPE_NULL:
                return null;
            case TYPE_REFERENCE:
                return "@" + data;
            case TYPE_ATTRIBUTE:
                return "?" + data;
            case TYPE_FLOAT:
                return Float.toString(Float.intBitsToFloat(data));
            case TYPE_DIMENSION:
                return complexToFloat(data) + DIMENSION_UNIT_STRS[
                        (data >> COMPLEX_UNIT_SHIFT) & COMPLEX_UNIT_MASK];
            case TYPE_FRACTION:
                return complexToFloat(data) * 100 + FRACTION_UNIT_STRS[
                        (data >> COMPLEX_UNIT_SHIFT) & COMPLEX_UNIT_MASK];
            case TYPE_INT_HEX:
                return "0x" + Integer.toHexString(data);
            case TYPE_INT_BOOLEAN:
                return data != 0 ? "true" : "false";
        }

        if (type >= TYPE_FIRST_COLOR_INT && type <= TYPE_LAST_COLOR_INT) {
            return "#" + Integer.toHexString(data);
        } else if (type >= TYPE_FIRST_INT && type <= TYPE_LAST_INT) {
            return Integer.toString(data);
        }

        return null;
    }

    private static final float MANTISSA_MULT =
            1.0f / (1 << TypedValue.COMPLEX_MANTISSA_SHIFT);
    private static final float[] RADIX_MULTS = new float[]{
            1.0f * MANTISSA_MULT, 1.0f / (1 << 7) * MANTISSA_MULT,
            1.0f / (1 << 15) * MANTISSA_MULT, 1.0f / (1 << 23) * MANTISSA_MULT
    };

    /**
     * Retrieve the base value from a complex data integer.  This uses the
     * {@link #COMPLEX_MANTISSA_MASK} and {@link #COMPLEX_RADIX_MASK} fields of
     * the data to compute a floating point representation of the number they
     * describe.  The units are ignored.
     *
     * @param complex A complex data value.
     * @return A floating point value corresponding to the complex data.
     */
    private static float complexToFloat(int complex) {
        return (complex & (TypedValue.COMPLEX_MANTISSA_MASK
                << TypedValue.COMPLEX_MANTISSA_SHIFT))
                * RADIX_MULTS[(complex >> TypedValue.COMPLEX_RADIX_SHIFT)
                & TypedValue.COMPLEX_RADIX_MASK];
    }

}
