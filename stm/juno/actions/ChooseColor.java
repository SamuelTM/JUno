package stm.juno.actions;

import stm.juno.cards.CardColor;

public class ChooseColor extends Action {

    private final CardColor color;

    public ChooseColor(CardColor color) {
        this.color = color;
    }

    public CardColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChooseColor) {
            return color.equals(((ChooseColor) obj).color);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Choose color " + color;
    }
}
