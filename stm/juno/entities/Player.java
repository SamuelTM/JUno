package stm.juno.entities;

import stm.juno.cards.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final List<Card> cards;

    public Player() {
        cards = new ArrayList<>();
    }

    public Player(Player toCopy) {
        cards = new ArrayList<>();
        cards.addAll(toCopy.getCards());
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
