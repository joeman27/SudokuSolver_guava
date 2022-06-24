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
 * Used to read, represent, solve, and output Sudoku puzzles.
 * @author Joe Mankovecky
 */
public class Sudoku {
    private MutableGraph<Node> puzzle;
    private Map<Integer, Node> nodes;
    private Path inputPath;

    private static Logger logger = LoggerFactory.getLogger(Sudoku.class.getName());

    /**
     * Initializes MutableGraph representing state of the puzzle. Values must be
     * populated via readFile().
     */
    public Sudoku() {
        puzzle = GraphBuilder.undirected().allowsSelfLoops(false).build();
    }

    /**
     * @return immutable copy of MutableGraph representing current state of the
     *         puzzle
     */
    public ImmutableGraph<Node> getGraph() {
        return ImmutableGraph.copyOf(puzzle);
    }

    /**
     * Produces file containing formatted contents of 9x9 sudoku grid.
     * 
     * @param path path (including file name) where the output file should be
     *             created
     */
    public void produceFile(Path path) {
        logger.debug("Entering produceFile");
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
            logger.debug("Leaving produceFile");
        }

    }

    /**
     * Reads Sudoku input file, converting it to a char array to be passed to
     * buildGraph().
     * 
     * @param path Relative path to Sudoku input file.
     * @throws InvalidPuzzleException if input puzzle does not fit Sudoku dimensions
     */
    public void readFile(Path path) {
        logger.debug("Entering readFile");
        this.inputPath = path;

        try {
            BufferedReader f = Files.newBufferedReader(inputPath);

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
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.debug(e.getStackTrace().toString());
        } finally {
            logger.debug("Leaving readFile");
        }
    }

    /**
     * Solves puzzle in place using a graph coloring strategy.
     * 
     * @throws InvalidPuzzleException if algorithm fails to find a solution
     */
    public void solve() {
        // Find first blank Node
        if (solveHelper(nextBlankNode(0))) {
            logger.info("Solution found");
        } else {
            throw new InvalidPuzzleException("Puzzle not solveable");
        }
    }

    /**
     * Handles recursive calls for solve().
     * 
     * @param id id of Node contained within puzzle graph from which to recurse
     * @return true if a solution is found, false otherwise
     */
    private boolean solveHelper(int id) {
        // Return true if we've reached the last Node (cell) of the puzzle
        if (id >= nodes.size()) {
            return true;
        }
        for (int v = 1; v <= 9; v++) {
            Node node = nodes.get(id);
            node.setValue(v);
            if (isValidValue(node)) {
                // Only return if further recursion results in a solution, otherwise try next
                // value
                if (solveHelper(nextBlankNode(id))) {
                    return true;
                }
            }
        }

        // Revert Node to unsolved state if no values tried were valid
        nodes.get(id).setValue(0);
        return false;
    }

    /**
     * Find id of next unsolved Node
     */
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
     * @param rowNum number of row currently being verified
     * @param line   contents of row currently being verified
     * @throws InvalidPuzzleException if input puzzle does not fit Sudoku dimensions
     *                                or includes invalid characters
     */
    private void verifyPuzzle(int rowNum, String line) {
        if (rowNum >= 9) {
            throw new InvalidPuzzleException("Invalid column length: " + rowNum);
        }

        if (line.length() != 9) {
            throw new InvalidPuzzleException("Invalid row length: " + line.length());
        }

        // Match rows against any number of 'X's and numbers only
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
        logger.debug("Entering buildGraph");

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
        logger.debug("Leaving buildGraph");
    }

}
