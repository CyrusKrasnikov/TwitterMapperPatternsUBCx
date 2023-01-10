package tests.filters;

import filters.*;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the parser.
 */
public class TestParser {
    @Test
    public void testBasic() throws SyntaxError {
        Filter f = new Parser("trump").parse();
        assertTrue(f instanceof BasicFilter);
        assertEquals("trump", ((BasicFilter) f).getWord());
    }

    @Test
    public void testHairy() throws SyntaxError {
        Filter x = new Parser("trump and (evil or blue) and red or green and not not purple").parse();
        assertEquals("(((trump and (evil or blue)) and red) or (green and not not purple))", x.toString());
    }

    @Test
    public void testAnd() throws SyntaxError {
        Filter x = new Parser("football and volleyball and not hockey").parse();
        assertTrue(x instanceof AndFilter);
        assertEquals("((football and volleyball) and not hockey)", x.toString());
    }

    @Test
    public void testOr() {
        Filter x;
        try {
            x = new Parser("not potato or apple or lemon")
                    .parse();
            assertTrue(x instanceof OrFilter);
            assertEquals("((not potato or apple) or lemon)", x.toString());
        } catch (SyntaxError e) {
            fail("Got SyntaxError when we shouldn't have");
        }
    }

    @Test()
    public void testSyntaxError(){
        try {
            new Parser("(missing right paren").parse();
            fail("Should throw SyntaxError");
        } catch (SyntaxError e) {
            out.println("OK, caught exception: "+e);
        }
    }

    @Test()
    public void testFilterParse(){
        Filter f = Filter.parse("(missing right paren");
        assertTrue(f instanceof BasicFilter);
        assertEquals("(missing right paren", f.toString());
    }

    @Test()
    public void testExtraStuffAtEnd(){
        Filter x = null;
        try {
            x = new Parser("(football and volleyball) abcd").parse();
            fail("Should throw SyntaxError");
        } catch (SyntaxError e) {
            out.println("OK, caught exception: "+e);
        }
        assertNull(x);
    }
}