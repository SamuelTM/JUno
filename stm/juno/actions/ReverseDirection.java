package stm.juno.actions;

import stm.juno.Uno;

public class ReverseDirection extends Action {

    @Override
    public void execute(Uno game, boolean verbose) {
        game.getPlayers().reverseDirection();
        game.getDiscardPile().setPendingAction(false);
        if (verbose) {
            System.out.println("Game direction has been reversed to " + (game.getPlayers().isClockwiseMotion()
                    ? "clockwise" : "counter-clockwise") + " motion");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReverseDirection;
    }

    @Override
    public String toString() {
        return "Reverse direction";
    }
}
