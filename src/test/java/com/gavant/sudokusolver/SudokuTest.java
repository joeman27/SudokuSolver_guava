package com.gavant.sudokusolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.google.common.graph.ImmutableGraph;

class SudokuTest {

    @Test
    void testInvalidFileInputs() {
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleEmpty.txt");
            new Sudoku().readFile(path);
        },
                "Expected error on empty puzzle");

        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleMissingRow.txt");
            new Sudoku().readFile(path);
        },
                "Expected error on short puzzle");

        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleExtraRow.txt");
            new Sudoku().readFile(path);
        },
                "Expected error on extra tall puzzle");

        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleLongRow.txt");
            new Sudoku().readFile(path);
        },
                "Expected error on extra wide puzzle");

        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleShortRow.txt");
            new Sudoku().readFile(path);
        },
                "Expected error on narrow puzzle");

        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleInvalidCharacter.txt");
            new Sudoku().readFile(path);
        },
                "Expected error on puzzle with invalid character");

    }

    @Test
    void testGraphBuild() {
        Sudoku sudoku = new Sudoku();
        Path path = Paths.get("src/test/resources/TestPuzzleValid.txt");
        sudoku.readFile(path);
        ImmutableGraph<Node> graph = sudoku.getGraph();

        for (Node node : graph.nodes()) {
            assertEquals(20, graph.degree(node),
                    "Expected 20 connections per node");
        }
    }

    @Test
    void testSolve() {
        Sudoku sudoku = new Sudoku();
        Path path = Paths.get("src/test/resources/TestPuzzleValid.txt");
        sudoku.readFile(path);
        sudoku.solve();
        ImmutableGraph<Node> graph = sudoku.getGraph();

        // Confirm there are no unsolved Nodes
        for (Node node : graph.nodes()) {
            assertNotEquals(0, node.getValue());
        }

        assertThrows(InvalidPuzzleException.class, () -> {
            Path p = Paths.get("src/test/resources/TestPuzzleNoSolution.txt");
            Sudoku s = new Sudoku();
            s.readFile(p);
            s.solve();
        },
                "Expected error on puzzle with no solution");
    }

    @Test
    void testProduceFile() {
        Sudoku sudoku = new Sudoku();
        Path path = Paths.get("src/test/resources/TestPuzzleValid.txt");
        sudoku.readFile(path);
        sudoku.solve();
        path = Paths.get("src/test/resources/TestPuzzleValid.sln.txt");
        sudoku.produceFile(path);
        assertNotNull(new File(path.toString()));
    }
}
