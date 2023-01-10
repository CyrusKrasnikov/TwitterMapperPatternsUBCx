package filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates filter with operator and two operands. Derived classes override getOperator() and matches() to specify operator.
 */
abstract class BinaryOperatorFilter implements Filter {

    /**
     * Subclasses override this method used at the toString()
     * @return corresponding string which represents operator
     */
    protected abstract String getOperator();
    private final Filter left;
    private final Filter right;

    BinaryOperatorFilter(Filter left, Filter right) {
        this.left = left;
        this.right = right;
    }

    public Filter getLeft() {
        return left;
    }

    public Filter getRight() {
        return right;
    }

    /**
     * EFFECTS: Collects all terms from the left and the right filters
     * @return List of collected terms
     */
    @Override
    public List<String> terms() {
        List<String> result = new ArrayList<>(getLeft().terms());
        result.addAll(getRight().terms());
        return result;
    }

    /**
     * EFFECTS: Combines string representations of the left filter, operator and the right filter
     * @return String according to syntax "(left operator right)"
     */
    public String toString() {
        return "(" + getLeft() + " " + getOperator() + " " + getRight() + ")";
    }

}
