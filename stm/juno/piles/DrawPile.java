package stm.juno.piles;

import stm.juno.cards.Card;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class DrawPile extends Stack<Card> {

    private final boolean verbose;

    public DrawPile(Card[] deck, boolean verbose) {
        addAll(Arrays.asList(deck));
        Collections.shuffle(this);
        this.verbose = verbose;
    }

    public DrawPile(DrawPile toCopy) {
        addAll(toCopy);
        this.verbose = false;
    }

    public Card[] draw(int nCards, DiscardPile discardPile) throws UnsupportedOperationException {
        Card[] cards = new Card[nCards];

        boolean notEnoughCardsInThePile = size() < nCards;
        boolean discardPileHasCardsToTakeFrom = discardPile.size() > (nCards - size());
        if (notEnoughCardsInThePile && discardPileHasCardsToTakeFrom) {
            Card topCard = !isEmpty() ? pop() : null;

            // Let's add the cards from the discard pile back to the draw pile
            Card topDiscardPileCard = discardPile.pop();

            int discardPileSize = discardPile.size();

            for (int i = 0; i < discardPileSize; i++) {
                push(discardPile.pop());
            }
            // But don't forget to keep the last card played on the discard pile
            discardPile.push(topDiscardPileCard);

            if (verbose) {
                System.out.println("We added the cards from the discard pile back to the draw pile");
            }
            Collections.shuffle(this);

            if (topCard != null) {
                push(topCard);
            }
        } else if (notEnoughCardsInThePile) {
            // We aren't supposed to get to this point but oh well
            throw new UnsupportedOperationException("There aren't enough cards to be taken from the draw pile");
        }

        for (int i = 0; i < nCards; i++) {
            cards[i] = pop();
        }

        return cards;
    }

    public Card peekNext() {
        return !isEmpty() ? peek() : null;
    }
}
