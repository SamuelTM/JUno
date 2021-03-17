package stm.juno.entities;

import java.util.ArrayList;

public class Players extends ArrayList<Player> {

    private int currentPlayerIndex;
    private boolean clockwiseMotion;

    public Players() {
        this.clockwiseMotion = true;
        this.currentPlayerIndex = 0;
    }

    public Players(Players toCopy) {
        currentPlayerIndex = toCopy.currentPlayerIndex;
        clockwiseMotion = toCopy.clockwiseMotion;

        for (Player p : toCopy) {
            add(new Player(p));
        }
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return get(currentPlayerIndex);
    }

    public void reverseDirection() {
        clockwiseMotion = !clockwiseMotion;
    }

    public boolean isClockwiseMotion() {
        return clockwiseMotion;
    }

    public void nextPlayer() {
        currentPlayerIndex = (clockwiseMotion ? currentPlayerIndex + 1 : currentPlayerIndex + size() - 1) % size();
    }

    public int[] getTotalOfCardsHeldByEachPlayer() {
        int[] totals = new int[size()];
        for (int i = 0; i < size(); i++) {
            totals[i] = get(i).getCards().size();
        }

        return totals;
    }
}
