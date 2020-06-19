package stm.juno.cards;

public enum Card {

    RED_0(CardType.NUMBER, CardColor.RED, 0),
    RED_1(CardType.NUMBER, CardColor.RED, 1),
    RED_2(CardType.NUMBER, CardColor.RED, 2),
    RED_3(CardType.NUMBER, CardColor.RED, 3),
    RED_4(CardType.NUMBER, CardColor.RED, 4),
    RED_5(CardType.NUMBER, CardColor.RED, 5),
    RED_6(CardType.NUMBER, CardColor.RED, 6),
    RED_7(CardType.NUMBER, CardColor.RED, 7),
    RED_8(CardType.NUMBER, CardColor.RED, 8),
    RED_9(CardType.NUMBER, CardColor.RED, 9),

    GREEN_0(CardType.NUMBER, CardColor.GREEN, 0),
    GREEN_1(CardType.NUMBER, CardColor.GREEN, 1),
    GREEN_2(CardType.NUMBER, CardColor.GREEN, 2),
    GREEN_3(CardType.NUMBER, CardColor.GREEN, 3),
    GREEN_4(CardType.NUMBER, CardColor.GREEN, 4),
    GREEN_5(CardType.NUMBER, CardColor.GREEN, 5),
    GREEN_6(CardType.NUMBER, CardColor.GREEN, 6),
    GREEN_7(CardType.NUMBER, CardColor.GREEN, 7),
    GREEN_8(CardType.NUMBER, CardColor.GREEN, 8),
    GREEN_9(CardType.NUMBER, CardColor.GREEN, 9),

    BLUE_0(CardType.NUMBER, CardColor.BLUE, 0),
    BLUE_1(CardType.NUMBER, CardColor.BLUE, 1),
    BLUE_2(CardType.NUMBER, CardColor.BLUE, 2),
    BLUE_3(CardType.NUMBER, CardColor.BLUE, 3),
    BLUE_4(CardType.NUMBER, CardColor.BLUE, 4),
    BLUE_5(CardType.NUMBER, CardColor.BLUE, 5),
    BLUE_6(CardType.NUMBER, CardColor.BLUE, 6),
    BLUE_7(CardType.NUMBER, CardColor.BLUE, 7),
    BLUE_8(CardType.NUMBER, CardColor.BLUE, 8),
    BLUE_9(CardType.NUMBER, CardColor.BLUE, 9),

    YELLOW_0(CardType.NUMBER, CardColor.YELLOW, 0),
    YELLOW_1(CardType.NUMBER, CardColor.YELLOW, 1),
    YELLOW_2(CardType.NUMBER, CardColor.YELLOW, 2),
    YELLOW_3(CardType.NUMBER, CardColor.YELLOW, 3),
    YELLOW_4(CardType.NUMBER, CardColor.YELLOW, 4),
    YELLOW_5(CardType.NUMBER, CardColor.YELLOW, 5),
    YELLOW_6(CardType.NUMBER, CardColor.YELLOW, 6),
    YELLOW_7(CardType.NUMBER, CardColor.YELLOW, 7),
    YELLOW_8(CardType.NUMBER, CardColor.YELLOW, 8),
    YELLOW_9(CardType.NUMBER, CardColor.YELLOW, 9),

    RED_REVERSE(CardType.REVERSE, CardColor.RED, 20),
    GREEN_REVERSE(CardType.REVERSE, CardColor.GREEN, 20),
    BLUE_REVERSE(CardType.REVERSE, CardColor.BLUE, 20),
    YELLOW_REVERSE(CardType.REVERSE, CardColor.YELLOW, 20),

    RED_SKIP(CardType.SKIP, CardColor.RED, 20),
    GREEN_SKIP(CardType.SKIP, CardColor.GREEN, 20),
    BLUE_SKIP(CardType.SKIP, CardColor.BLUE, 20),
    YELLOW_SKIP(CardType.SKIP, CardColor.YELLOW, 20),

    RED_DRAW_TWO(CardType.DRAW_TWO, CardColor.RED, 20),
    GREEN_DRAW_TWO(CardType.DRAW_TWO, CardColor.GREEN, 20),
    BLUE_DRAW_TWO(CardType.DRAW_TWO, CardColor.BLUE, 20),
    YELLOW_DRAW_TWO(CardType.DRAW_TWO, CardColor.YELLOW, 20),

    WILD(CardType.WILD, null, 50),
    WILD_DRAW_FOUR(CardType.WILD_DRAW_FOUR, null, 50);

    private final CardType type;
    private final CardColor color;
    private final int value;

    Card(CardType type, CardColor color, int value) {
        this.type = type;
        this.color = color;
        this.value = value;
    }

    public CardType getType() {
        return type;
    }

    public CardColor getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public static Card[] getDeck() {
        final Card[] deck = new Card[108];
        int currentIndex = 0;
        final Card[] deckStart = new Card[]{RED_0, BLUE_0, YELLOW_0, GREEN_0, WILD, WILD, WILD_DRAW_FOUR,
                WILD_DRAW_FOUR};

        for(Card c : deckStart) {
            deck[currentIndex] = c;
            currentIndex++;
        }

        for (int i = 0; i < 2; i++) {
            for (Card c : values()) {
                if (c.getValue() > 0) {
                    deck[currentIndex] = c;
                    currentIndex++;
                }
            }
        }

        return deck;
    }
}
