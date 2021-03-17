package stm.juno.actions;

public class DrawCard extends Action {

    private final int nCards;

    public DrawCard(int nCards) {
        this.nCards = nCards;
    }

    public int getNumCards() {
        return nCards;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DrawCard) {
            return nCards == ((DrawCard) obj).nCards;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Draw " + nCards + " cards";
    }
}
