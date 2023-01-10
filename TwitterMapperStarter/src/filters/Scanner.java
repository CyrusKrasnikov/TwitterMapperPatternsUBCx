package filters;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple scanner for a language consisting of words and parentheses.
 */
public class Scanner {
    private final List<String> tokens;
    private final Pattern tokenPattern;

    /**
     * Constructor compiles regular expression and initializes tokens list as linked list.
     */
    public Scanner() {
        // This pattern matches words ([a-zA-Z]+), left and right parens, and whitespace
        tokenPattern = Pattern.compile("\\(|\\)|[a-zA-Z]+|\\s+");

        tokens = new LinkedList<>();
        // Actual tokenization is extracted from this constructor to the dedicated method
    }

    /**
     * This simple scanner scans the entire input in its constructor, adding tokens to a list
     * which it then returns as necessary in response to calls to its peek and advance methods.
     *
     * @param input String to scan
     */
    public void tokenize(String input) {
        Matcher m = tokenPattern.matcher(input);
        while (m.find()) {
            String token = m.group();
            // Throw away any white space
            if (token.matches("\\s+"))  continue;
            tokens.add(token);
        }
    }


    /**
     * Return the first token remaining, without changing anything.
     * A second call to peek without an intervening call to advance, will return this same token again.
     * @return      The first remaining token in the input, or null if no tokens remain
     */
    public String peek() {
        return tokens.size() > 0 ? tokens.get(0) : null;
    }

    /**
     * Advance the input, consuming the current token, and return the first remaining token in the input.
     * @return      The first remaining token in the input after advancing, or null if no tokens remain
     */
    public String advance() {
        tokens.remove(0);
        return tokens.size() > 0 ? tokens.get(0) : null;
    }
}
