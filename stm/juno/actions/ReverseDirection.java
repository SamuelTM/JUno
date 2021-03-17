package stm.juno.actions;

public class ReverseDirection extends Action {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReverseDirection;
    }

    @Override
    public String toString() {
        return "Reverse direction";
    }
}
