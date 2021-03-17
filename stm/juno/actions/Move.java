package stm.juno.actions;

import java.util.Arrays;

public class Move {

    private final Action[] actions;

    public Move(Action... actions) {
        this.actions = actions;
    }

    public Action[] getActions() {
        return actions;
    }

    @Override
    public String toString() {
        return Arrays.toString(actions);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move move = (Move) obj;
            if (move.actions.length == actions.length) {
                for (int i = 0; i < move.actions.length; i++) {
                    if (!move.actions[i].equals(actions[i])) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }
}
