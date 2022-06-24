package com.gavant.sudokusolver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
    /**
     * Entry point for SudokuSolver.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        File inputLocation = new File("src/main/resources/puzzles");

        for (String fileName : inputLocation.list()) {
            Path path = Paths.get("src/main/resources/puzzles/" + fileName);
            Sudoku puzzle = new Sudoku();
            puzzle.readFile(path);
            puzzle.solve();

            path = Paths.get("src/main/resources/solved/" + fileName + ".sln.txt");
            puzzle.produceFile(path);
        }
    }
}
