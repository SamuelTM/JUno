package stm.juno.actions;

public class SkipPlayer extends Action {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkipPlayer;
    }

    @Override
    public String toString() {
        return "Skip player";
    }
}
