package stm.juno.moves;

import stm.juno.cards.Card;
import stm.juno.cards.CardColor;

public class TestCard {

    private final Card card;
    private final CardColor testColor;

    public TestCard(Card card, CardColor testColor) {
        this.card = card;
        this.testColor = testColor;
    }

    public Card getCard() {
        return card;
    }

    public CardColor getTestColor() {
        return testColor;
    }

    public boolean isWildCard() {
        return (card == null || card.getColor() == null) && testColor != null;
    }
}
