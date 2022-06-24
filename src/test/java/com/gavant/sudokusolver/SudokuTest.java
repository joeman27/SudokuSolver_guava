package com.gavant.sudokusolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.google.common.graph.ImmutableGraph;

/**
 * Unit tests for Sudoku class.
 */
class SudokuTest {

    @Test
    void testFileInput() {
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleMissingRow");
            new Sudoku().readFile(path);}, 
            "Expected error on short puzzle");

        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleExtraRow");
            new Sudoku().readFile(path);}, 
            "Expected error on extra tall puzzle");
        
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleLongRow");
            new Sudoku().readFile(path);}, 
            "Expected error on extra wide puzzle");
    
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleShortRow");
            new Sudoku().readFile(path);}, 
            "Expected error on narrow puzzle");
                
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleInvalidCharacter");
            new Sudoku().readFile(path);}, 
            "Expected error on puzzle with invalid character");
    }

    @Test
    void testGraphBuild() {
        Sudoku sudoku = new Sudoku();
        Path path = Paths.get("src/test/resources/TestPuzzleValid");
        sudoku.readFile(path);
        ImmutableGraph<Node> graph = sudoku.getGraph();

        for (Node node : graph.nodes()) {
            assertEquals(20, graph.degree(node),
                "Expected 20 connections per node");
        }
    }
}
