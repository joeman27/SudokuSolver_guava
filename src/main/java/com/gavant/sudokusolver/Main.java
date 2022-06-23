package com.gavant.sudokusolver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 */
public final class Main {
    /**
     * Entry point for SudokuSolver.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        // final String[] fileNames = {"puzzle1", "puzzle2", "puzzle3", "puzzle4", "puzzle5"};
        File inputLocation = new File("src/main/resources/");

        for (String fileName : inputLocation.list()) {
            Path path = Paths.get("src/main/resources/" + fileName);
            Sudoku puzzle = new Sudoku(path);
        }
    }
}
