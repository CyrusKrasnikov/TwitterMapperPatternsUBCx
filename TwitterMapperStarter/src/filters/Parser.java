package filters;

/**
 * Parse a string in the filter language and return the filter.
 * Throws a SyntaxError exception on failure.
 * <p>
 * This is a top-down recursive descent parser (a.k.a., LL(1))
 * <p>
 * The really short explanation is that an LL(1) grammar can be parsed by a collection
 * of mutually recursive methods, each of which is able to recognize a single grammar symbol.
 * <p>
 * The grammar (EBNF) for our filter language is:
 * <p>
 * goal    ::= expr
 * expr    ::= orexpr
 * orexpr  ::= andexpr ( "or" andexpr )*
 * andexpr ::= notexpr ( "and" notexpr )*
 * notexpr ::= prim | "not" notexpr
 * prim    ::= word | "(" expr ")"
 * <p>
 * The reason for writing it this way is that it respects the "natural" precedence of boolean
 * expressions, where the precedence order (decreasing) is:
 *      parens
 *      not
 *      and
 *      or
 * This allows an expression like:
 *      blue or green and not red or yellow and purple
 * To be parsed like:
 *      blue or (green and (not red)) or (yellow and purple)
 */
public class Parser { // Task 3: Connect your new classes to the filter parser
    private final Scanner scanner;
    private static final String LPAREN = "(";
    private static final String RPAREN = ")";
    private static final String OR = "or";
    private static final String AND = "and";
    private static final String NOT = "not";

    public Parser(String input) {
        scanner = new Scanner();
        scanner.tokenize(input); // To be improved: tokenize input string in another method instead of the constructor
    }

    /**
     * EFFECTS: Creates filter instance corresponding to the text expression (query) entered by user into search box
     * @return Filter which represents expression
     * @throws SyntaxError if there is extra characters at the end of search query
     */
    public Filter parse() throws SyntaxError {
        Filter ans = expr();
        if (scanner.peek() != null) {
            throw new SyntaxError("Extra stuff at end of input");
        }
        return ans;
    }

    private Filter expr() throws SyntaxError {
        return orexpr();
    }

    private Filter orexpr() throws SyntaxError {
        Filter sub = andexpr();
        String token = scanner.peek();
        while (token != null && token.equals(OR)) {
            scanner.advance();
            Filter right = andexpr();
            // At this point we have two subexpressions ("sub" on the left and "right" on the right)
            // that are to be connected by "or"
            // The new filter object should be assigned to the variable "sub"
            sub = new OrFilter(sub,right);
            token = scanner.peek();
        }
        return sub;
    }

    private Filter andexpr() throws SyntaxError {
        Filter sub = notexpr();
        String token = scanner.peek();
        while (token != null && token.equals(AND)) {
            scanner.advance();
            Filter right = notexpr();
            // At this point we have two subexpressions ("sub" on the left and "right" on the right)
            // that are to be connected by "and"
            // The new filter object should be assigned to the variable "sub"
            sub = new AndFilter(sub,right);
            token = scanner.peek();
        }
        return sub;
    }

    private Filter notexpr() throws SyntaxError {
        String token = scanner.peek();
        if (token.equals(NOT)) {
            scanner.advance();
            Filter sub = notexpr();
            return new NotFilter(sub);
        } else {
            return prim();
        }
    }

    private Filter prim() throws SyntaxError {
        String token = scanner.peek();
        if (token.equals(LPAREN)) {
            scanner.advance();
            Filter sub = expr();
            if (!scanner.peek().equals(RPAREN)) {
                throw new SyntaxError("Expected ')'");
            }
            scanner.advance();
            return sub;
        } else {
            Filter sub = new BasicFilter(token);
            scanner.advance();
            return sub;
        }
    }
}
