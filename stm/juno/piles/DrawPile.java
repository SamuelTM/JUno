package stm.juno.piles;

import stm.juno.cards.Card;
import stm.juno.cards.CardType;

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

        if (discardPile != null) {
            if (size() < nCards && discardPile.size() >= (nCards - size())) {
                Card top = getNext();

                if (top != null) {
                    pop();
                }

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

                if (top != null) {
                    push(top);
                }
            } else if (size() < nCards && discardPile.size() < (nCards - size())) {
                // We aren't supposed to get to this point but oh well
                throw new UnsupportedOperationException("There aren't enough cards to be taken from the draw pile");
            }

            for (int i = 0; i < nCards; i++) {
                cards[i] = pop();
            }
        }

        return cards;
    }

    public Card getStartingCard() {
        Card firstCard;

        while ((firstCard = pop()).getType().equals(CardType.WILD_DRAW_FOUR)) {
            push(firstCard);
        }

        return firstCard;
    }

    public Card getNext() {
        return peek();
    }
}
