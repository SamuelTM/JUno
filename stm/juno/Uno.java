package stm.juno;

import stm.juno.actions.*;
import stm.juno.cards.Card;
import stm.juno.cards.CardColor;
import stm.juno.cards.CardType;
import stm.juno.entities.Player;
import stm.juno.entities.Players;
import stm.juno.piles.DiscardPile;
import stm.juno.piles.DrawPile;
import stm.juno.search.SearchAlg;

import java.util.*;
import java.util.stream.DoubleStream;

public class Uno {

    private final DrawPile drawPile;
    private final DiscardPile discardPile;
    private final Players players;

    private final double[] scores;
    private int[] lastTotalOfCardsHeldByEachPlayer;
    private int totalTurns;

    private final boolean verbose;

    private final Random random;

    public Uno(int nPlayers, boolean verbose) {
        this.drawPile = new DrawPile(Card.getDeck(), verbose);

        Card firstCard;

        while ((firstCard = drawPile.remove(drawPile.size() - 1)).getType().equals(CardType.WILD_DRAW_FOUR)) {
            drawPile.add(0, firstCard);
        }

        this.discardPile = new DiscardPile(firstCard);
        this.players = new Players(nPlayers, 7, this);

        this.scores = new double[nPlayers];
        this.lastTotalOfCardsHeldByEachPlayer = new int[nPlayers];

        this.verbose = verbose;

        this.random = new Random();
    }

    public Uno(Uno toCopy) {
        drawPile = new DrawPile(toCopy.drawPile);
        players = new Players(toCopy.players);
        discardPile = new DiscardPile(toCopy.discardPile);

        totalTurns = toCopy.totalTurns;

        scores = new double[toCopy.scores.length];
        System.arraycopy(toCopy.scores, 0, scores, 0, toCopy.scores.length);

        lastTotalOfCardsHeldByEachPlayer = new int[toCopy.lastTotalOfCardsHeldByEachPlayer.length];
        System.arraycopy(toCopy.lastTotalOfCardsHeldByEachPlayer, 0, lastTotalOfCardsHeldByEachPlayer, 0,
                toCopy.lastTotalOfCardsHeldByEachPlayer.length);

        verbose = toCopy.verbose;
        random = new Random();
    }

    public int getWinnerIndex() {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getCards().isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private List<Integer> getCompatibleCardsIndexes(List<Card> currentPlayerCards, Card testCard,
                                                    CardColor testColor) {
        final List<Integer> results = new ArrayList<>();
        final HashMap<Card, Integer> uniqueCards = new HashMap<>();
        for (int i = 0; i < currentPlayerCards.size(); i++) {
            Card c = currentPlayerCards.get(i);
            if (c.getValue() == 50) {
                uniqueCards.putIfAbsent(c, i);
            } else {
                if (testCard != null && testCard.getColor() != null) {
                    if (c.getColor().equals(testCard.getColor())) {
                        uniqueCards.putIfAbsent(c, i);
                    } else {
                        boolean sameType = c.getType().equals(testCard.getType()),
                                isNumber = c.getType().equals(CardType.NUMBER),
                                sameValue = c.getValue() == testCard.getValue();
                        if ((sameType && !isNumber) || (isNumber && sameValue)) {
                            uniqueCards.putIfAbsent(c, i);
                        }
                    }
                } else if (testColor != null) {
                    if (c.getColor().equals(testColor)) {
                        uniqueCards.putIfAbsent(c, i);
                    }
                }
            }
        }

        for (Card card : uniqueCards.keySet()) {
            results.add(uniqueCards.get(card));
        }

        return results;
    }

    private boolean isCardValidAsNextMove(Card card) {
        if (card != null) {
            Player currentPlayer = players.getCurrentPlayer();
            Card lastPlayed = discardPile.getLastPlayed();
            CardColor lastColor = discardPile.getLastColor();

            currentPlayer.getCards().add(card);
            int lastWithdrawalIndex = currentPlayer.getCards().size() - 1;
            List<Integer> compatibleCardsIndexes = getCompatibleCardsIndexes(currentPlayer.getCards(), lastPlayed,
                    lastColor);
            boolean validCard = !compatibleCardsIndexes.isEmpty() && compatibleCardsIndexes
                    .get(compatibleCardsIndexes.size() - 1).equals(lastWithdrawalIndex);
            currentPlayer.getCards().remove(lastWithdrawalIndex);

            return validCard;
        }
        return false;
    }

    public List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        if (discardPile.isPendingAction()) {
            switch (discardPile.getLastPlayed().getType()) {
                case WILD:
                    for (CardColor color : CardColor.values()) {
                        final List<Integer> compatibleCardsIndexes = getCompatibleCardsIndexes(
                                players.getCurrentPlayer().getCards(), null, color);
                        if (compatibleCardsIndexes.size() > 0) {
                            for (int cardIndex : compatibleCardsIndexes) {
                                Card possibleCard = players.getCurrentPlayer().getCards().get(cardIndex);
                                if (possibleCard.getColor() == null) {
                                    for (CardColor nextColor : CardColor.values()) {
                                        // We can choose the color, play a wild or wild draw four and choose the color
                                        possibleMoves.add(new Move(new ChooseColor(color),
                                                new PlayCard(cardIndex, possibleCard), new ChooseColor(nextColor)));
                                    }
                                } else {
                                    // Or we can choose the color and play any other card that isn't a wild
                                    possibleMoves.add(new Move(new ChooseColor(color),
                                            new PlayCard(cardIndex, possibleCard)));
                                }
                            }
                        } else {
                            // If we don't have a suitable card, we choose the color and draw one from the pile
                            possibleMoves.add(new Move(new ChooseColor(color), new DrawCard(1)));
                        }

                        Card nextOnPile = drawPile.getNext();
                        if (isCardValidAsNextMove(nextOnPile)) {
                            if (nextOnPile.getColor() == null) {
                                for (CardColor nextColor : CardColor.values()) {
                                    // We can also choose the color, draw a card that is a wild or wild draw four,
                                    // play it and choose the color
                                    possibleMoves.add(new Move(new ChooseColor(color), new DrawCard(1),
                                            new PlayCard(-players.getCurrentPlayer().getCards().size(), nextOnPile),
                                            new ChooseColor(nextColor)));
                                }
                            } else {
                                // And we can choose the color, draw a card that isn't a wild but can be played
                                // and play it
                                possibleMoves.add(new Move(new ChooseColor(color), new DrawCard(1),
                                        new PlayCard(-players.getCurrentPlayer().getCards().size(), nextOnPile)));
                            }
                        }
                    }
                    break;
                case WILD_DRAW_FOUR:
                    possibleMoves.add(new Move(new DrawCard(4)));
                    break;
                case DRAW_TWO:
                    possibleMoves.add(new Move(new DrawCard(2)));
                    break;
                case REVERSE:
                    possibleMoves.add(new Move(new ReverseDirection()));
                    break;
                case SKIP:
                    possibleMoves.add(new Move(new SkipPlayer()));
                    break;
            }
        } else {
            Card nextOnPile = drawPile.getNext();
            if (isCardValidAsNextMove(nextOnPile)) {
                if (nextOnPile.getColor() == null) {
                    for (CardColor nextColor : CardColor.values()) {
                        // We can draw a wild or wild draw four, play it and choose the color
                        possibleMoves.add(new Move(new DrawCard(1),
                                new PlayCard(-players.getCurrentPlayer().getCards().size(), nextOnPile),
                                new ChooseColor(nextColor)));
                    }
                } else {
                    // We can draw a card that isn't a wild but can be played, then play it
                    possibleMoves.add(new Move(new DrawCard(1),
                            new PlayCard(-players.getCurrentPlayer().getCards().size(), nextOnPile)));
                }
            }
            List<Integer> compatibleCardsIndexes = getCompatibleCardsIndexes(players.getCurrentPlayer().getCards(),
                    discardPile.getLastPlayed(), discardPile.getLastColor());
            if (!compatibleCardsIndexes.isEmpty()) {
                for (int compatibleCardIndex : compatibleCardsIndexes) {
                    Card possibleCard = players.getCurrentPlayer().getCards().get(compatibleCardIndex);
                    if (possibleCard.getColor() == null) {
                        for (CardColor color : CardColor.values()) {
                            // We can play a wild or wild draw four and choose the color
                            possibleMoves.add(new Move(new PlayCard(compatibleCardIndex, possibleCard),
                                    new ChooseColor(color)));
                        }
                    } else {
                        // Or we can just play any other available card
                        possibleMoves.add(new Move(new PlayCard(compatibleCardIndex, possibleCard)));
                    }
                }
            } else {
                // If we don't have a suitable card to play, we're going to need to draw one from the pile
                possibleMoves.add(new Move(new DrawCard(1)));
            }
        }

        return possibleMoves;
    }

    public void printStatus(List<Move> possibleMoves) {
        System.out.println(String.format("----- PLAYER %d - turn %d -----", players.getCurrentPlayerIndex(),
                totalTurns));
        System.out.println("Last card played: " + discardPile.getLastPlayed().toString());
        System.out.println("Last color: " + (discardPile.getLastColor() != null
                ? discardPile.getLastColor().toString() : "none"));
        System.out.println("Pending action: " + discardPile.isPendingAction());
        System.out.println("Number of cards on the draw pile: " + drawPile.size());
        System.out.println("Number of cards on the discard pile: " + discardPile.size());
        System.out.println("Current player's cards: " + players.getCurrentPlayer().getCards().toString());
        System.out.println("Number of possible moves: " + possibleMoves.size());
        for (Move m : possibleMoves) {
            System.out.println(m.toString());
        }
    }

    private void updateScores() {
        double[] results = new double[scores.length];
        System.arraycopy(scores, 0, results, 0, scores.length);

        int[] differences = new int[players.size()];
        int[] currentTotalOfCardsHeldByEachPlayer = players.getTotalOfCardsHeldByEachPlayer();

        for (int i = 0; i < lastTotalOfCardsHeldByEachPlayer.length; i++) {
            differences[i] = lastTotalOfCardsHeldByEachPlayer[i] - currentTotalOfCardsHeldByEachPlayer[i];
        }

        for (int i = 0; i < differences.length; i++) {
            double difference = differences[i];
            for (int j = 0; j < lastTotalOfCardsHeldByEachPlayer.length; j++) {
                if (difference != 0) {
                    if (i == j) {
                        results[j] = results[j] + difference;
                    } else {
                        results[j] = results[j] + ((difference / (players.size() - 1)) * -1);
                    }
                }
            }
        }

        System.arraycopy(results, 0, scores, 0, results.length);
    }

    public void executeMove(Move move, boolean verbose) {
        lastTotalOfCardsHeldByEachPlayer = players.getTotalOfCardsHeldByEachPlayer();
        move.execute(this, verbose);

        updateScores();
        totalTurns++;
        players.nextPlayer();

        if (verbose) {
            System.out.println("Current scores: " + Arrays.toString(scores));
            System.out.println("Sum of scores: " + DoubleStream.of(scores).sum());
        }
    }

    private void chooseMove() {
        List<Move> possibleMoves = getPossibleMoves();
        if (verbose) {
            printStatus(possibleMoves);
        }

        Move choice;

        if (players.getCurrentPlayerIndex() == 0) {
            //choice = SearchAlg.NMax(this, 0).x;
            choice = SearchAlg.hypermax(this, 0, SearchAlg.getAlpha(players.size())).x;
        } else {
            choice = possibleMoves.get(random.nextInt(possibleMoves.size()));
        }

        executeMove(choice, verbose);
    }

    public boolean nextMove() {
        int winner = getWinnerIndex();

        if (winner == -1) {
            chooseMove();
            return true;
        } else {
            if (verbose) {
                System.out.println("Game over, player " + winner + " won in " + totalTurns + " turns");
            }
            return false;
        }
    }

    public DrawPile getDrawPile() {
        return drawPile;
    }

    public DiscardPile getDiscardPile() {
        return discardPile;
    }

    public Players getPlayers() {
        return players;
    }

    public double[] getScores() {
        return scores;
    }
}
