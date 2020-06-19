package stm.juno.actions;

import stm.juno.Uno;
import stm.juno.cards.Card;

import java.util.Arrays;

public class DrawCard extends Action {

    private final int nCards;

    public DrawCard(int nCards) {
        this.nCards = nCards;
    }

    @Override
    public void execute(Uno game, boolean verbose) {
        Card[] cards = game.getDrawPile().draw(nCards, game.getDiscardPile());
        game.getPlayers().getCurrentPlayer().getCards().addAll(Arrays.asList(cards));
        game.getDiscardPile().setPendingAction(false);
        if (verbose) {
            System.out.println(nCards + " cards have been withdrawn from the draw pile");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DrawCard) {
            return nCards == ((DrawCard) obj).nCards;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Draw " + nCards + " cards";
    }
}
