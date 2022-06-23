package com.gavant.sudokusolver;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class SudokuTest {

    @Test
    void testFileInput() {
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleExtraRow");
            new Sudoku(path);}, 
            "Expected error on extra tall puzzle");
        
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleLongRow");
            new Sudoku(path);}, 
            "Expected error on extra wide puzzle");
                
        assertThrows(InvalidPuzzleException.class, () -> {
            Path path = Paths.get("src/test/resources/TestPuzzleInvalidCharacter");
            new Sudoku(path);}, 
            "Expected error on puzzle with invalid character");

        
    }
}
