package com.gavant.sudokusolver;

/**
 * InvalidPuzzleException is thrown when an input Sudoku puzzle does not fit
 *  expected dimensions or contains invalid characters.
 */

public class InvalidPuzzleException extends RuntimeException {
    public InvalidPuzzleException() {
        super();
    }

    public InvalidPuzzleException(String message) {
        super(message);
    }
}
