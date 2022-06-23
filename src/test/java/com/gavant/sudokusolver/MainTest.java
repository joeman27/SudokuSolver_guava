package com.gavant.sudokusolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class MainTest {
    final String[] fileNames = {"puzzle1", "puzzle2", "puzzle3", "puzzle4", "puzzle5"};
    /**
     * Rigorous Test.
     */
    @Test
    void testApp() {
        assertEquals(1, 1);
    }

    void testFileInput() {
        File inputLocation = new File("src/test/resources");
        for (String fileName : inputLocation.list()) {
            Path path = Paths.get("src/test/resources" + fileName);
            Sudoku puzzle = new Sudoku(path);
        }
    }
}
