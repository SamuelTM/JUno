package stm.juno.actions;

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

    public int getCardIndex() {
        return cardIndex;
    }

    public Card getCard() {
        return card;
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
