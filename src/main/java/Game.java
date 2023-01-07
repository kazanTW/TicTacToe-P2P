import java.util.*;

public class Game{
    protected ArrayList<String> players;
    protected int tableSide;
    protected int status;
    protected int round;
    private final int winCondition;
    private int counter;
    private final String[][] table;

    public Game() {
        this.counter = 0;
        this.status = 0;
        this.round = 0;
        this.winCondition = 3;
        this.tableSide = 3;
        this.table = new String[tableSide][tableSide];
        this.players = new ArrayList<>();
        players.add("O");
        players.add("X");
    }

    public void round(int xPosition, int yPosition) {
        round %= 2;
        if (table[xPosition][yPosition] == null) {
            setValue(xPosition, yPosition, players.get(round));
            if (checkDiagonalLeft(xPosition, yPosition) || checkDiagonalRight(xPosition, yPosition) || checkStraight(xPosition, yPosition)) status = 1;
            counter++;
            if (counter == tableSide * tableSide && status != 1) status = -1;
            round++;
        } else status = -2;
    }

    boolean checkDiagonalLeft(int xPosition, int yPosition) {
        int counter = 0;
        String target = table[xPosition][yPosition];
        int xLeftTop = xPosition;
        int yLeftTop = yPosition;

        while (xLeftTop != 0 && yLeftTop != 0) {
            xLeftTop--;
            yLeftTop--;
        }
        for (int i = 0; i < tableSide; i++) {
            if (xLeftTop + i >= tableSide || yLeftTop + i >= tableSide) break;
            if (Objects.equals(table[xLeftTop + i][yLeftTop + i], target)) counter++;
            else counter = 0;
            if (counter == winCondition) return true;
        }
        return false;
    }

    boolean checkDiagonalRight(int xPosition, int yPosition) {
        int counter = 0;
        String target = table[xPosition][yPosition];
        int xRightTop = xPosition;
        int yRightTop = yPosition;

        while (xRightTop != 0 && yRightTop != tableSide - 1) {
            xRightTop--;
            yRightTop++;
        }
        for (int i = 0; i < tableSide; i++) {
            if (xRightTop + i >= tableSide || yRightTop - i < 0) break;
            if (Objects.equals(table[xRightTop + i][yRightTop - i], target)) counter++;
            else counter = 0;
            if (counter == winCondition) return true;
        }
        return false;
    }

    boolean checkStraight(int xPosition, int yPosition) {
        int counter = 0;
        String target = table[xPosition][yPosition];

        for (int i = 0; i < tableSide; i++) {
            if (Objects.equals(table[i][yPosition], target)) counter++;
            else counter = 0;
            if (counter == winCondition) return true;
        }
        for (int i = 0; i < tableSide; i++) {
            if (Objects.equals(table[xPosition][i], target)) counter++;
            else counter = 0;
            if (counter == winCondition) return true;
        }
        return false;
    }

    public void setValue(int xPosition, int yPosition, String player) {
        table[xPosition][yPosition] = player;
    }
}
