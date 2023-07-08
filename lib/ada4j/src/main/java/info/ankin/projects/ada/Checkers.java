package info.ankin.projects.ada;

/**
 * URL specific checkers used within Ada.
 * <p>
 * validation functions
 */
interface Checkers {
    /**
     * Assuming that x is an ASCII letter, this function returns the lower case
     * equivalent.
     */
    static char toLower(char x) {
        return Character.toLowerCase(x);
    }

    /**
     * Returns true if the character is an ASCII letter. Equivalent to std::isalpha.
     */
    static boolean isAlpha(char x) {
        return Character.isAlphabetic(x);
    }

    /**
     * Check whether a string starts with 0x or 0X. The function is only
     * safe if input.size() >=2.
     *
     * @see #hasHexPrefix(CharSequence)
     */
    static boolean hasHexPrefixUnsafe(CharSequence input) {
        return input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    /**
     * Check whether a string starts with 0x or 0X.
     */
    static boolean hasHexPrefix(CharSequence input) {
        return input.length() > 2 && hasHexPrefixUnsafe(input);
    }

    /**
     * Check whether x is an ASCII digit.
     */
    static boolean isDigit(char x) {
        return Character.isDigit(x);
    }

    /**
     * A string starts with a Windows drive letter if the following are all true:
     * <p>
     * - its length is greater than or equal to 2
     * - its first two code points are a Windows drive letter
     * - its length is 2 or its third code point is U+002F (/), U+005C (\), U+003F
     * (?), or U+0023 (#).
     * <p>
     * <a href="https://url.spec.whatwg.org/#start-with-a-windows-drive-letter">
     * https://url.spec.whatwg.org/#start-with-a-windows-drive-letter</a>
     */
    static boolean isWindowsDriveLetter(CharSequence input) {
        return input.length() >= 2 &&
                (isAlpha(input.charAt(0)) && ((input.charAt(1) == ':') || (input.charAt(1) == '|'))) &&
                ((input.length() == 2) || (input.charAt(2) == '/' || input.charAt(2) == '\\' ||
                        input.charAt(2) == '?' || input.charAt(2) == '#'));
    }

    /**
     * A normalized Windows drive letter is a Windows drive letter of which
     * the second code point is U+003A (:).
     */
    static boolean isNormalizedWindowsDriveLetter(CharSequence input) {
        return input.length() >= 2 && (isAlpha(input.charAt(0)) && (input.charAt(1) == ':'));
    }

    /**
     * Returns a bitset. If the first bit is set, then at least one character needs
     * percent encoding. If the second bit is set, a \\ is found. If the third bit
     * is set then we have a dot. If the fourth bit is set, then we have a percent
     * character.
     */
    static byte pathSignature(CharSequence input) {
        return 1;
    }

    /**
     * Will be removed when Ada supports C++20.
     */
    static boolean beginsWith(CharSequence view,
                              CharSequence prefix) {
        return false;
    }

    /**
     * Returns true if an input is an ipv4 address.
     */
    static boolean isIpv4(CharSequence view) {
        return false;
    }

    /**
     * Returns true if the length of the domain name and its labels are according to
     * the specifications. The length of the domain must be 255 octets (253
     * characters not including the last 2 which are the empty label reserved at the
     * end). When the empty label is included (a dot at the end), the domain name
     * can have 254 characters. The length of a label must be at least 1 and at most
     * 63 characters.
     *
     * @see <a href=https://www.rfc-editor.org/rfc/rfc1034>
     * section 3.1. of https://www.rfc-editor.org/rfc/rfc1034</a>
     * @see <a href=https://www.unicode.org/reports/tr46/#ToASCII>
     * https://www.unicode.org/reports/tr46/#ToASCII</a>
     */
    static boolean verifyDnsLength(CharSequence input) {
        return false;
    }

}
