package stm.juno.piles;

import stm.juno.cards.Card;
import stm.juno.cards.CardColor;
import stm.juno.cards.CardType;

import java.util.Stack;

public class DiscardPile extends Stack<Card> {

    private CardColor lastColor;
    private boolean pendingAction;

    public DiscardPile(Card first) {
        push(first);
        lastColor = first.getColor();
        pendingAction = !first.getType().equals(CardType.NUMBER);
    }

    public DiscardPile(DiscardPile toCopy) {
        lastColor = toCopy.lastColor;
        pendingAction = toCopy.pendingAction;
        addAll(toCopy);
    }

    public boolean isPendingAction() {
        return pendingAction;
    }

    public void setPendingAction(boolean pendingAction) {
        this.pendingAction = pendingAction;
    }

    public void setLastColor(CardColor lastColor) {
        this.lastColor = lastColor;
    }

    public CardColor getLastColor() {
        return lastColor;
    }

    public void discard(Card card) {
        push(card);
        lastColor = card.getColor();
        pendingAction = !card.getType().equals(CardType.NUMBER) && !card.getType().equals(CardType.WILD);
    }

    public Card getLastPlayed() {
        return peek();
    }
}
