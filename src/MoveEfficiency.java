public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        return this.numberOfEmptyTiles == o.numberOfEmptyTiles ? Integer.compare(this.score, o.score) : Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);
/*        if (this.numberOfEmptyTiles == o.numberOfEmptyTiles) {
            if (this.score == o.score) {
                return 0;
            }
            else {
                return o.score - this.score;
            }
        }
        else {
            return o.numberOfEmptyTiles - this.numberOfEmptyTiles;
        }*/
    }
}
