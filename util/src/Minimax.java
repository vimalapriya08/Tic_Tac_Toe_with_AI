package tictactoe;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.print("Input command: ");
            String[] input = scanner.nextLine().split(" ");
            if (input[0].equals("exit")) {
                break;
            }
            if (input.length == 3 && input[0].equals("start")) {
                String player1 = input[1];
                String player2 = input[2];
                if (isValidPlayer(player1) && isValidPlayer(player2)) {
                    playGame(player1, player2);
                } else {
                    System.out.println("Bad parameters!");
                }
            } else {
                System.out.println("Bad parameters!");
            }
        }
    }

    private static boolean isValidPlayer(String player) {
        return player.equals("easy") || player.equals("medium") || player.equals("hard") || player.equals("user");
    }

    private static void playGame(String player1, String player2) {
        char[][] board = {
                {' ', ' ', ' '},
                {' ', ' ', ' '},
                {' ', ' ', ' '}
        };
        printBoard(board);

        boolean player1Turn = true;
        while (true) {
            if (player1Turn) {
                makeMove(board, player1, 'X');
            } else {
                makeMove(board, player2, 'O');
            }
            printBoard(board);
            String result = checkGameState(board);
            if (!result.equals("Game not finished")) {
                System.out.println(result);
                break;
            }
            player1Turn = !player1Turn;
        }
    }

    private static void printBoard(char[][] board) {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    private static void makeMove(char[][] board, String playerType, char symbol) {
        switch (playerType) {
            case "user":
                userMove(board, symbol);
                break;
            case "easy":
                System.out.println("Making move level \"easy\"");
                randomMove(board, symbol);
                break;
            case "medium":
                System.out.println("Making move level \"medium\"");
                mediumMove(board, symbol);
                break;
            case "hard":
                System.out.println("Making move level \"hard\"");
                aiMoveHard(board, symbol);
                break;
        }
    }

    private static void userMove(char[][] board, char symbol) {
        while (true) {
            System.out.print("Enter the coordinates: ");
            String[] input = scanner.nextLine().split(" ");
            try {
                int row = Integer.parseInt(input[0]) - 1;
                int col = Integer.parseInt(input[1]) - 1;
                if (row < 0 || row > 2 || col < 0 || col > 2) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else if (board[row][col] != ' ') {
                    System.out.println("This cell is occupied! Choose another one!");
                } else {
                    board[row][col] = symbol;
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
            }
        }
    }

    private static void randomMove(char[][] board, char symbol) {
        while (true) {
            int row = (int) (Math.random() * 3);
            int col = (int) (Math.random() * 3);
            if (board[row][col] == ' ') {
                board[row][col] = symbol;
                break;
            }
        }
    }

    private static void mediumMove(char[][] board, char symbol) {
        // 1. Check for an immediate win for the AI.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = symbol; // AI attempts to place its symbol
                    if (checkWin(board, symbol)) {
                        return; // If this move results in a win, take it.
                    }
                    board[i][j] = ' '; // Undo the move
                }
            }
        }

        // 2. Check if the opponent is about to win, and block them.
        char opponentSymbol = (symbol == 'X') ? 'O' : 'X'; // Opponent's symbol
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = opponentSymbol; // Simulate opponent's move
                    if (checkWin(board, opponentSymbol)) {
                        board[i][j] = symbol; // Block the opponent by taking this spot
                        return;
                    }
                    board[i][j] = ' '; // Undo the move
                }
            }
        }

        // 3. If no winning or blocking move is found, make a random move.
        randomMove(board, symbol);
    }


    private static void aiMoveHard(char[][] board, char symbol) {
        int[] bestMove = minimax(board, symbol == 'X', 0);
        int row = bestMove[1];
        int col = bestMove[2];
        board[row][col] = symbol;
    }

    private static int[] minimax(char[][] board, boolean isMaximizing, int depth) {
        char currentSymbol = isMaximizing ? 'X' : 'O';
        char opponentSymbol = isMaximizing ? 'O' : 'X';

        String result = checkGameState(board);
        if (result.equals("X wins")) return new int[]{10 - depth, -1, -1};
        if (result.equals("O wins")) return new int[]{depth - 10, -1, -1};
        if (result.equals("Draw")) return new int[]{0, -1, -1};

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestRow = -1;
        int bestCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = currentSymbol;
                    int score = minimax(board, !isMaximizing, depth + 1)[0];
                    board[i][j] = ' ';
                    if (isMaximizing ? score > bestScore : score < bestScore) {
                        bestScore = score;
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
        }
        return new int[]{bestScore, bestRow, bestCol};
    }

    private static boolean checkWin(char[][] board, char symbol) {
        return (board[0][0] == symbol && board[0][1] == symbol && board[0][2] == symbol) ||
                (board[1][0] == symbol && board[1][1] == symbol && board[1][2] == symbol) ||
                (board[2][0] == symbol && board[2][1] == symbol && board[2][2] == symbol) ||
                (board[0][0] == symbol && board[1][0] == symbol && board[2][0] == symbol) ||
                (board[0][1] == symbol && board[1][1] == symbol && board[2][1] == symbol) ||
                (board[0][2] == symbol && board[1][2] == symbol && board[2][2] == symbol) ||
                (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
                (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol);
    }

    private static String checkGameState(char[][] board) {
        if (checkWin(board, 'X')) {
            return "X wins";
        } else if (checkWin(board, 'O')) {
            return "O wins";
        } else if (isBoardFull(board)) {
            return "Draw";
        }
        return "Game not finished";
    }

    private static boolean isBoardFull(char[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
