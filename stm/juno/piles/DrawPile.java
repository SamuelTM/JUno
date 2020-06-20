package stm.juno.piles;

import stm.juno.cards.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DrawPile extends ArrayList<Card> {

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
                // Let's add the cards from the discard pile back to the draw pile
                Card top = getNext();

                if (top != null) {
                    remove(size() - 1);
                }

                int discardPileSize = discardPile.size();
                // But don't forget to keep the last card played on the discard pile
                for (int i = 0; i < discardPileSize - 1; i++) {
                    add(discardPile.remove(0));
                }

                if (verbose) {
                    System.out.println("We added the cards from the discard pile back to the draw pile");
                }
                Collections.shuffle(this);

                if (top != null) {
                    add(top);
                }
            } else if (size() < nCards && discardPile.size() < (nCards - size())) {
                // We aren't supposed to get to this point but oh well
                throw new UnsupportedOperationException("There aren't enough cards to be taken from the draw pile");
            }

            for (int i = 0; i < nCards; i++) {
                cards[i] = remove(size() - 1);
            }
        }

        return cards;
    }

    public Card getNext() {
        return !isEmpty() ? get(size() - 1) : null;
    }
}
