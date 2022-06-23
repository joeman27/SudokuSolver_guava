package com.gavant.sudokusolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

/**
 * 
 */
public class Sudoku {
    private MutableGraph puzzle = GraphBuilder.undirected().allowsSelfLoops(false).build();

    private static Logger logger = LoggerFactory.getLogger(Sudoku.class.getName());

    /**
     * 
     * @param path
     */
    public Sudoku(Path path) {
        readPuzzle(path);
    }

    /**
     * 
     * @param fileName
     */
    public void produceFile(String fileName) {}

    /**
     * 
     * @param path
     */
    private void readPuzzle(Path path) {
        logger.debug("Entering readPuzzle");
        try{
            BufferedReader f = Files.newBufferedReader(path);

            // Intermediary storage between puzzle as a file and as a graph
            int[][] grid = new int[9][9];
            int rowNum = 0;
            String line;

            // Convert file to two dimensional array
            while ((line = f.readLine()) != null) {
                line = line.strip();
                verifyPuzzle(rowNum, line);
                for (int colNum = 0; colNum < line.length(); colNum++) {
                    grid[rowNum][colNum] = line.charAt(colNum);
                }
                rowNum++;
            }

            buildGraph(grid);
            f.close();
        }
        catch (IOException e) {
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
        }
        catch (InvalidPuzzleException e) {
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
            System.exit(1);
        }
        finally {
            logger.debug("Leaving readPuzzle");
        }
    }

    /**
     * 
     * @param rowNum
     * @param line
     * @throws InvalidPuzzleException
     */
    private void verifyPuzzle(int rowNum, String line) throws InvalidPuzzleException{
        if (rowNum >= 9) {
            throw new InvalidPuzzleException("Invalid column length: " + rowNum);
        }

        if (line.length() != 9) {
            throw new InvalidPuzzleException("Invalid row length: " + line.length());
        }

        Pattern p = Pattern.compile("^[xX\\d]{9}$");
        Matcher m = p.matcher(line);
        if (!m.matches()) {
            throw new InvalidPuzzleException("Line contains invalid characters: " + line);
        }

    }

    /**
     * 
     */
    private void buildGraph(int[][] grid) {

    }

}
