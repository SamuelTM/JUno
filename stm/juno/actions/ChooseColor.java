package stm.juno.actions;

import stm.juno.Uno;
import stm.juno.cards.CardColor;

public class ChooseColor extends Action {

    private final CardColor color;

    public ChooseColor(CardColor color) {
        this.color = color;
    }

    @Override
    public void execute(Uno game, boolean verbose) {
        game.getDiscardPile().setLastColor(color);
        if (verbose) {
            System.out.println("Player " + game.getPlayers().getCurrentPlayerIndex() + " chose the color " + color);
        }
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
