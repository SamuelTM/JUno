package stm.juno.entities;

import stm.juno.Uno;

import java.util.ArrayList;
import java.util.Arrays;

public class Players extends ArrayList<Player> {

    private final int nPlayers;
    private final int cardsPerPlayer;
    private int currentPlayerIndex;
    private boolean clockwiseMotion;

    public Players(int nPlayers, int cardsPerPlayer, Uno game) {
        this.nPlayers = nPlayers;
        this.cardsPerPlayer = cardsPerPlayer;
        this.clockwiseMotion = true;

        for (int i = 0; i < nPlayers; i++) {
            Player player = new Player();
            player.getCards().addAll(Arrays.asList(game.getDrawPile().draw(cardsPerPlayer, game.getDiscardPile())));
            add(player);
        }
    }

    public Players(Players toCopy) {
        nPlayers = toCopy.nPlayers;
        cardsPerPlayer = toCopy.cardsPerPlayer;
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
        currentPlayerIndex = (clockwiseMotion ? currentPlayerIndex + 1 : currentPlayerIndex + nPlayers - 1) % nPlayers;
    }

    public int[] getTotalOfCardsHeldByEachPlayer() {
        int[] totals = new int[nPlayers];
        for (int i = 0; i < nPlayers; i++) {
            totals[i] = get(i).getCards().size();
        }

        return totals;
    }
}
