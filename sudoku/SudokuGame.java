import java.util.Random;
import java.util.Scanner;

public class SudokuGame {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private int[][] board = new int[SIZE][SIZE];
    private int[][] initialBoard = new int[SIZE][SIZE];
    private Random random = new Random();

    public SudokuGame(String difficulty) {
        generateFullBoard();
        int cellsToRemove = getCellsToRemove(difficulty);
        removeNumbers(cellsToRemove);
        copyBoard(initialBoard, board);
    }

    private int getCellsToRemove(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return 20;
            case "moderate":
                return 40;
            case "hard":
                return 55;
            case "god":
                return 64;
            default:
                return 40;
        }
    }

    private void copyBoard(int[][] dest, int[][] src) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                dest[i][j] = src[i][j];
            }
        }
    }

    private void generateFullBoard() {
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

    public void printBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
    }

    public boolean solve() {
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

    public boolean isSolutionValid() {
        int[][] copy = new int[SIZE][SIZE];
        copyBoard(copy, board);
        return solve() && validateSolvedBoard();
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

    public void resetBoard() {
        copyBoard(board, initialBoard);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Sudoku!");
        System.out.print("Select difficulty level (easy, moderate, hard, god): ");
        String difficulty = scanner.nextLine().toLowerCase();

        SudokuGame game = new SudokuGame(difficulty);

        while (true) {
            System.out.println("\nCurrent Sudoku Board:");
            game.printBoard();

            System.out.println("\nOptions:");
            System.out.println("1. Check Solution");
            System.out.println("2. Reset Board");
            System.out.println("3. Solve Board");
            System.out.println("4. Enter a number");
            System.out.println("5. Quit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    if (game.isSolutionValid()) {
                        System.out.println("The solution is valid!");
                    } else {
                        System.out.println("The solution is not valid.");
                    }
                    break;
                case 2:
                    game.resetBoard();
                    System.out.println("Board reset to initial state.");
                    break;
                case 3:
                    if (game.solve()) {
                        System.out.println("Sudoku Board Solved:");
                        game.printBoard();
                    } else {
                        System.out.println("No solution exists.");
                    }
                    break;
                case 4:
                    System.out.print("Enter row (1-9): ");
                    int row = scanner.nextInt() - 1;
                    System.out.print("Enter column (1-9): ");
                    int col = scanner.nextInt() - 1;
                    System.out.print("Enter number (1-9): ");
                    int num = scanner.nextInt();
                    if (row >= 0 && row < SIZE && col >= 0 && col < SIZE && num > 0 && num <= SIZE) {
                        game.board[row][col] = num;
                    } else {
                        System.out.println("Invalid input. Try again.");
                    }
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
