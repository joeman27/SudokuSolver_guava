package com.gavant.sudokusolver;

import java.util.Objects;

public final class Node {
    private final int id;
    private int value;

    /**
     * Node constructor specifying only the required ID field. Should only
     *  be used for blank Sudoku cells.
     * @param i unique id to assign to Node; 0 <= i <= 81
     */
    public Node(int i) {
        this.id = i;
    }

    /**
     * Node constructor specifying both the required ID field as well as
     *  Sudoku cell value.
     * @param i unique id to assign to Node; 0 <= i <= 81
     * @param v value to store within Node; 1 <= v <= 9
     */
    public Node(int i, int v) {
        this.id = i;
        this.value = v;
    }

    /**
     * @return unique id of this Node
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * @return current value stored by Node; 0 if unset/unsolved
     */
    public int getValue() {
        return this.value;
    }

    /**
     * @param v value to store within Node; 1 <= v <= 9
     */
    public void setValue(int v) {
        this.value = v;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Node){
            Node node = (Node) other;
            return this.getId() == node.getId()
                && this.getValue() == node.getValue();
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getValue());
    }
}
