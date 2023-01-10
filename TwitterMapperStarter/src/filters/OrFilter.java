package filters;

import twitter4j.Status;

/**
 * A filter that represents the logical or of its two child filters
 */
public class OrFilter extends BinaryOperatorFilter { // Task 2: Complete the implementation of the filter package

    public OrFilter(Filter left, Filter right){
        super(left,right);
    }

    /**
     * An or filter matches when either child matches
     * @param s     the tweet to check
     * @return      whether or not it matches
     */
    @Override
    public boolean matches(Status s) {
        return getLeft().matches(s) || getRight().matches(s);
    }

    /**
     * @return "or" string
     */
    @Override
    protected String getOperator() {
        return "or";
    }
}
