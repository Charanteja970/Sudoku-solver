import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SudokuGameGUI extends JFrame {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private int[][] board = new int[SIZE][SIZE];
    private int[][] initialBoard = new int[SIZE][SIZE];
    private Random random = new Random();
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private String difficulty;

    public SudokuGameGUI(String difficulty) {
        this.difficulty = difficulty;
        generateAndInitializeBoard(this.difficulty);
        initializeGUI();
    }

    private void generateAndInitializeBoard(String difficulty) {
        generateFullBoard(difficulty);
        int cellsToRemove = getCellsToRemove(difficulty);
        removeNumbers(cellsToRemove);
        copyBoard(initialBoard, board);
    }

    private int getCellsToRemove(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return 20;
            case "medium":
                return 40;
            case "hard":
                return 55;
            case "expert":
                return 64;
            case "evil":
                return 70;
            default:
                return 40;
        }
    }

    private void copyBoard(int[][] dest, int[][] src) {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, SIZE);
        }
    }

    private void generateFullBoard(String difficulty) {
        fillDiagonal();
        fillRemaining(0, SUBGRID_SIZE);
    }

    private void fillDiagonal() {
        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) {
            fillSubgrid(i, i);
        }
    }

    private void fillSubgrid(int row, int col) {
        boolean[] used = new boolean[SIZE + 1];
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                int num;
                do {
                    num = random.nextInt(SIZE) + 1;
                } while (used[num]);
                used[num] = true;
                board[row + i][col + j] = num;
            }
        }
    }

    private boolean fillRemaining(int i, int j) {
        if (j >= SIZE && i < SIZE - 1) {
            i++;
            j = 0;
        }
        if (i >= SIZE && j >= SIZE) {
            return true;
        }
        if (i < SUBGRID_SIZE) {
            if (j < SUBGRID_SIZE) {
                j = SUBGRID_SIZE;
            }
        } else if (i < SIZE - SUBGRID_SIZE) {
            if (j == (i / SUBGRID_SIZE) * SUBGRID_SIZE) {
                j += SUBGRID_SIZE;
            }
        } else {
            if (j == SIZE - SUBGRID_SIZE) {
                i++;
                j = 0;
                if (i >= SIZE) {
                    return true;
                }
            }
        }
        for (int num = 1; num <= SIZE; num++) {
            if (isSafeToPlace(i, j, num)) {
                board[i][j] = num;
                if (fillRemaining(i, j + 1)) {
                    return true;
                }
                board[i][j] = 0;
            }
        }
        return false;
    }

    private boolean isSafeToPlace(int row, int col, int num) {
        return !isInRow(row, num) && !isInCol(col, num) && !isInBox(row - row % SUBGRID_SIZE, col - col % SUBGRID_SIZE, num);
    }

    private boolean isInRow(int row, int num) {
        for (int col = 0; col < SIZE; col++) {
            if (board[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean isInCol(int col, int num) {
        for (int row = 0; row < SIZE; row++) {
            if (board[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBox(int startRow, int startCol, int num) {
        for (int row = 0; row < SUBGRID_SIZE; row++) {
            for (int col = 0; col < SUBGRID_SIZE; col++) {
                if (board[startRow + row][startCol + col] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeNumbers(int count) {
        while (count > 0) {
            int cellId = random.nextInt(SIZE * SIZE);
            int row = cellId / SIZE;
            int col = cellId % SIZE;
            if (board[row][col] != 0) {
                board[row][col] = 0;
                count--;
            }
        }
    }

    private void initializeGUI() {
        setTitle("Sudoku Game");
        setSize(600, 600);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(SIZE, SIZE));

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                if (initialBoard[i][j] != 0) {
                    cells[i][j].setText(String.valueOf(initialBoard[i][j]));
                    cells[i][j].setEditable(false);
                } else {
                    cells[i][j].setText("");
                    cells[i][j].addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            char ch = e.getKeyChar();
                            if (!Character.isDigit(ch) || ch < '1' || ch > '9') {
                                e.consume();
                            }
                        }
                    });
                }

                if ((i / SUBGRID_SIZE + j / SUBGRID_SIZE) % 2 == 0) {
                    cells[i][j].setBackground(new Color(220, 220, 220));
                }

                boardPanel.add(cells[i][j]);
            }
        }

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton checkButton = new JButton("Check Solution");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSolutionValid()) {
                    JOptionPane.showMessageDialog(null, "The solution is valid!");
                } else {
                    JOptionPane.showMessageDialog(null, "The solution is not valid.");
                }
            }
        });

        JButton solveButton = new JButton("Solve Board");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solve();
                updateBoard();
            }
        });

        JButton clearButton = new JButton("Clear Board");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBoard();
                updateBoard();
            }
        });

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        controlPanel.add(checkButton);
        controlPanel.add(solveButton);
        controlPanel.add(clearButton);
        controlPanel.add(quitButton);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void updateBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (initialBoard[i][j] != 0) {
                    cells[i][j].setText(String.valueOf(initialBoard[i][j]));
                } else {
                    cells[i][j].setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j]));
                }
            }
        }
    }

    private void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (initialBoard[i][j] == 0) {
                    board[i][j] = 0;
                    cells[i][j].setText("");
                }
            }
        }
    }

    private boolean solve() {
        return solve(0, 0);
    }

    private boolean solve(int row, int col) {
        if (row == SIZE - 1 && col == SIZE) {
            return true;
        }
        if (col == SIZE) {
            row++;
            col = 0;
        }
        if (board[row][col] != 0) {
            return solve(row, col + 1);
        }
        for (int num = 1; num <= SIZE; num++) {
            if (isSafeToPlace(row, col, num)) {
                board[row][col] = num;
                if (solve(row, col + 1)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isSolutionValid() {
        int[][] copy = new int[SIZE][SIZE];
        copyBoard(copy, board);
        if (!solve()) {
            copyBoard(board, copy);
            return false;
        }
        copyBoard(board, copy);
        return validateSolvedBoard();
    }

    private boolean validateSolvedBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int num = board[row][col];
                board[row][col] = 0;
                if (!isSafeToPlace(row, col, num)) {
                    return false;
                }
                board[row][col] = num;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"easy", "medium", "hard", "expert", "evil"};
            String difficulty = (String) JOptionPane.showInputDialog(
                    null,
                    "Select difficulty level:",
                    "Sudoku Game",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]
            );

            if (difficulty != null) {
                new SudokuGameGUI(difficulty);
            } else {
                System.exit(0);
            }
        });
    }
}
