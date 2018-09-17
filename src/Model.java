import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    public Tile[][] gameTiles;
    protected int score;
    protected int maxTile;
    protected Stack<Tile[][]> previousStates;
    protected Stack<Integer> previousScores;
    boolean isSaveNeeded;

    private void saveState(Tile[][] tiles) {
        Tile[][] copy = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                copy[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(copy);
        previousScores.push(new Integer(score));
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public Model() {
        resetGameTiles();
        this.previousStates = new Stack<>();
        this.previousScores = new Stack<>();
        this.isSaveNeeded = true;
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            int index = (int) (Math.random() * emptyTiles.size());
            emptyTiles.get(index).value = (Math.random() < 0.9 ? 2 : 4);
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> result = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (this.gameTiles[i][j].isEmpty()) {
                    result.add(this.gameTiles[i][j]);
                }
            }
        }
        return result;
    }

    protected void resetGameTiles() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                this.gameTiles[i][j] = new Tile();
            }
        }
        this.score = 0;
        this.maxTile = 0;
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean result = false;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = i + 1; j < tiles.length; j++) {
                if (tiles[i].isEmpty() && !tiles[j].isEmpty()) {
                    result = true;
                    Tile temp = tiles[i];
                    tiles[i] = tiles[j];
                    tiles[j] = temp;
                    i++;
                }
            }
        }
        return result;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean result = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            if (tiles[i].value == tiles[i + 1].value && !tiles[i].isEmpty()) {
                result = true;
                tiles[i].value += tiles[i + 1].value;
                score += tiles[i].value;
                if (tiles[i].value > maxTile) {
                    maxTile = tiles[i].value;
                }
                tiles[i + 1].value = 0;
                compressTiles(tiles);
            }
        }
        return result;
    }

    protected void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean add = false;
        for (int i = 0; i < gameTiles.length; i++) {
            add |= compressTiles(gameTiles[i]);
            mergeTiles(gameTiles[i]);
        }
        if (add) {
            addTile();
        }
        isSaveNeeded = true;
    }

    protected void right() {
        saveState(gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }

    protected void up() {
        saveState(gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    protected void down() {
        saveState(gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }

    private void rotate() {
        int maxIndex = gameTiles.length - 1;
        Tile[][] temp = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i <gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                temp[j][maxIndex - i] = gameTiles[i][j];
            }
        }
        gameTiles = temp;
    }

    protected MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency moveEfficiency;
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        else {
            moveEfficiency = new MoveEfficiency(-1, score, move);
        }
        rollback();
        return moveEfficiency;
    }

    protected boolean hasBoardChanged() {
        return weight(gameTiles) != weight(previousStates.peek());
    }

    private int weight(Tile[][] tiles) {
        int result = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                result += tiles[i][j].value;
            }
        }
        return result;
    }

    protected void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        priorityQueue.peek().getMove().move();
    }

    protected void randomMove() {
        switch(((int) (Math.random() * 100)) % 4) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    protected boolean canMove() {
        boolean result = false;
        if (!getEmptyTiles().isEmpty()) {
            result = true;
        }
        else {
            for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    if (j != FIELD_WIDTH - 1) {
                        if (gameTiles[i][j].value == gameTiles[i][j + 1].value) {
                            return true;
                        }
                    }
                    if (i != FIELD_WIDTH - 1) {
                        if (gameTiles[i][j].value == gameTiles[i + 1][j].value) {
                            return true;
                        }
                    }
                }
            }
        }
        return result;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }
}
