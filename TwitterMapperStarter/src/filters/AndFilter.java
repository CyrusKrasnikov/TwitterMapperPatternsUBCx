package filters;

import twitter4j.Status;

/**
 * A filter that represents the logical "and" of its two child filters
 */
public class AndFilter extends BinaryOperatorFilter { // Task 2: Complete the implementation of the filter package

    public AndFilter(Filter left, Filter right) {
        super(left,right);
    }

    /**
     * An and filter matches when both children match
     * @param s     the tweet to check
     * @return      whether or not it matches
     */
    @Override
    public boolean matches(Status s) {
        return getLeft().matches(s) && getRight().matches(s);
    }

    /**
     * @return "and" string
     */
    @Override
    protected String getOperator() {
        return "and";
    }
}
