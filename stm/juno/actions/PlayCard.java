package stm.juno.actions;

import stm.juno.Uno;
import stm.juno.cards.Card;

public class PlayCard extends Action {

    private final boolean notWithdrawnYet;
    private final int cardIndex;
    private final Card card;

    public PlayCard(int cardIndex, Card card) {
        this.cardIndex = Math.abs(cardIndex);
        this.card = card;
        this.notWithdrawnYet = cardIndex < 0;
    }

    @Override
    public void execute(Uno game, boolean verbose) {
        Card move = game.getPlayers().getCurrentPlayer().getCards().remove(cardIndex);
        game.getDiscardPile().discard(move);
        if (verbose) {
            System.out.println("Player " + game.getPlayers().getCurrentPlayerIndex() + " played " + card);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayCard) {
            PlayCard playCard = (PlayCard) obj;
            return card.equals(playCard.card) && notWithdrawnYet == playCard.notWithdrawnYet
                    && cardIndex == playCard.cardIndex;
        }

        return false;
    }

    @Override
    public String toString() {
        if (!notWithdrawnYet) {
            return "Play " + card + " that's at index " + cardIndex;
        } else {
            return "Play " + card + " that we just withdrew";
        }
    }
}
