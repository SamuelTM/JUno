package stm.juno.actions;

import stm.juno.Uno;

public class SkipPlayer extends Action {
    @Override
    public void execute(Uno game, boolean verbose) {
        game.getDiscardPile().setPendingAction(false);
        if (verbose) {
            System.out.println("Player " + game.getPlayers().getCurrentPlayerIndex() + " has been skipped");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkipPlayer;
    }

    @Override
    public String toString() {
        return "Skip player";
    }
}
