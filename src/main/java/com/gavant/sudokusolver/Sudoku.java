package com.gavant.sudokusolver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

/**
 * 
 */
public class Sudoku {
    private MutableGraph<Node> puzzle;
    Map<Integer, Node> nodes;

    private static Logger logger = LoggerFactory.getLogger(Sudoku.class.getName());

    /**
     * 
     */
    public Sudoku() {
        puzzle = GraphBuilder.undirected().allowsSelfLoops(false).build();
    }

    public ImmutableGraph<Node> getGraph() {
        return ImmutableGraph.copyOf(puzzle);
    }

    /**
     * 
     * @param fileName
     */
    public void produceFile(Path path) {
        try {
            BufferedWriter f = Files.newBufferedWriter(path);

            int i = 0;
            StringBuilder line = new StringBuilder();
            for (Node node : getGraph().nodes()) {
                if (i % 9 == 0) {
                    line.append("\n");
                    f.write(line.toString());
                    line.setLength(0);
                }
                line.append(node.toString() + " ");
                i++;
            }
            line.append("\n");
            f.write(line.toString());

            f.close();

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
        } finally {

        }

    }

    /**
     * Reads Sudoku input file, converting it to a char array to be passed to
     * buildGraph()
     * 
     * @param path Relative path to Sudoku input file.
     */
    public void readFile(Path path) {
        logger.debug("Entering readFile");
        try {
            BufferedReader f = Files.newBufferedReader(path);

            // Intermediary storage between puzzle as a file and as a graph
            char[][] grid = new char[9][9];

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

            if (rowNum < 9) {
                throw new InvalidPuzzleException("Invalid column length: " + rowNum);
            }

            f.close();
            buildGraph(grid);
        } 
        catch (IOException e) {
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
        }
        catch (Exception e) {
            throw e;
        }
        // catch (InvalidPuzzleException e) {
        // logger.error(e.getMessage());
        // logger.debug(e.getStackTrace().toString());
        // System.exit(1);
        // }
        finally {
            logger.debug("Leaving readFile");
        }
    }

    /**
     * 
     */
    public void solve() {
        // Find first blank Node
        if (solveHelper(nextBlankNode(0))) {
            System.out.println("Solution found");
        } else {
            throw new InvalidPuzzleException("Puzzle not solveable");
        }
    }

    private boolean solveHelper(int id) {
        if (id >= nodes.size()) {
            return true;
        }
        for (int v = 1; v <= 9; v++) {
            Node node = nodes.get(id);
            node.setValue(v);
            if (isValidValue(node)) {
                if (solveHelper(nextBlankNode(id))) {
                    return true;
                }
            }
        }

        nodes.get(id).setValue(0);
        return false;
    }

    private int nextBlankNode(int id) {
        while (id < nodes.size() && nodes.get(id).getValue() != 0) {
            id++;
        }
        return id;
    }

    private boolean isValidValue(Node node) {
        for (Node neighbor : puzzle.adjacentNodes(node)) {
            if (neighbor.getValue() == node.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param rowNum
     * @param line
     * @throws InvalidPuzzleException
     */
    private void verifyPuzzle(int rowNum, String line) throws InvalidPuzzleException {
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
     * Constructs graph of Sudoku cells, connecting those that share rows, columns,
     * or 3x3 blocks.
     * 
     * @param grid Two-dimensional char array storing the contents of the Sudoku
     *             input file.
     */
    private void buildGraph(char[][] grid) {

        // Create nodes for each value in the grid, storing in a HashMap as they cannot
        // be
        // getted from the graph later.
        nodes = new HashMap<Integer, Node>();
        int id = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {

                Node node;
                if (grid[row][col] != 'X') {
                    node = new Node(id, Integer.valueOf(String.valueOf(grid[row][col])));
                } else {
                    node = new Node(id);
                }

                nodes.put(id, node);
                this.puzzle.addNode(node);
                id++;
            }
        }

        // CONNECT ROWS
        // For each row...
        for (int row = 0; row < grid.length; row++) {

            // ...and each column except the last...
            for (int col = 0; col < grid[0].length - 1; col++) {
                id = row * 9 + col;

                // ...connect the element at that row and column to all nodes in subsequent
                // columns of the same row.
                for (int otherCol = col + 1; otherCol < grid[0].length; otherCol++) {
                    int otherId = row * 9 + otherCol;
                    this.puzzle.putEdge(nodes.get(id), nodes.get(otherId));
                }
            }
        }

        // CONNECT COLUMNS
        // For each column...
        for (int col = 0; col < grid[0].length; col++) {

            // ...and each row except the last...
            for (int row = 0; row < grid.length - 1; row++) {
                id = row * 9 + col;

                // ...connect the element at that column and row to all nodes in subsequent rows
                // of the same column.
                for (int otherRow = row + 1; otherRow < grid.length; otherRow++) {
                    int otherId = otherRow * 9 + col;
                    this.puzzle.putEdge(nodes.get(id), nodes.get(otherId));
                }
            }
        }

        // CONNECT BLOCKS
        // Iterate through 3x3 blocks of the puzzle.
        // upperBound/leftBound define the upper/left sides of the block,
        // lower/right sides are calculated as +3.
        for (int upperBound = 0; upperBound < grid.length; upperBound += 3) {
            for (int leftBound = 0; leftBound < grid[0].length; leftBound += 3) {

                // Iterate through elements of the 3x3 block...
                for (int row = upperBound; row < upperBound + 3; row++) {
                    for (int col = leftBound; col < leftBound + 3; col++) {
                        id = row * 9 + col;

                        // ...and connect to every other element of the block.
                        for (int otherRow = upperBound; otherRow < upperBound + 3; otherRow++) {
                            for (int otherCol = leftBound; otherCol < leftBound + 3; otherCol++) {
                                int otherId = otherRow * 9 + otherCol;

                                // Disallow self-loops
                                if (id != otherId) {
                                    this.puzzle.putEdge(nodes.get(id), nodes.get(otherId));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
