package stm.juno;

import stm.juno.actions.*;
import stm.juno.cards.Card;
import stm.juno.cards.CardColor;
import stm.juno.cards.CardType;
import stm.juno.entities.Player;
import stm.juno.entities.Players;
import stm.juno.actions.Move;
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

        this.discardPile = new DiscardPile(this.drawPile.getStartingCard());
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

    private void chooseAndExecuteMove() {
        List<Move> possibleMoves = getPossibleMoves();
        if (verbose) {
            printStatus(possibleMoves);
        }

        Move choice;

        if (players.getCurrentPlayerIndex() == 0) {
            choice = SearchAlg.hypermax(this, 0, SearchAlg.getAlpha(players.size())).x;
        } else {
            choice = possibleMoves.get(random.nextInt(possibleMoves.size()));
        }

        executeMove(choice, verbose);
    }

    public void printStatus(List<Move> possibleMoves) {
        System.out.printf("----- PLAYER %d - turn %d -----%n", players.getCurrentPlayerIndex(),
                totalTurns);
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


    public boolean nextMove() {
        int winner = getWinnerIndex();

        if (winner == -1) {
            chooseAndExecuteMove();
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


    private List<Integer> getIndexesOfCardsCompatibleWithTestCardOrColor(List<Card> cards, Card testCard,
                                                                         CardColor testColor) {
        final HashMap<Card, Integer> uniqueCards = new HashMap<>();
        for (int i = 0; i < cards.size(); i++) {
            Card currentPlayerCard = cards.get(i);
            if (currentPlayerCard.isWildCard()) {
                uniqueCards.putIfAbsent(currentPlayerCard, i);
            } else {
                if (testCard != null && !testCard.isWildCard()) {
                    if (currentPlayerCard.getColor().equals(testCard.getColor())) {
                        uniqueCards.putIfAbsent(currentPlayerCard, i);
                    } else {
                        boolean sameType = currentPlayerCard.getType().equals(testCard.getType()),
                                isNumber = currentPlayerCard.getType().equals(CardType.NUMBER),
                                sameValue = currentPlayerCard.getValue() == testCard.getValue();
                        if ((sameType && !isNumber) || (isNumber && sameValue)) {
                            uniqueCards.putIfAbsent(currentPlayerCard, i);
                        }
                    }
                } else if (testColor != null) {
                    if (currentPlayerCard.getColor().equals(testColor)) {
                        uniqueCards.putIfAbsent(currentPlayerCard, i);
                    }
                }
            }
        }

        return new ArrayList<>(uniqueCards.values());
    }

    public List<Move> getPossibleMoves() {
        if (getDiscardPile().isPendingAction()) {
            return getMovesThatSolvePendingAction();
        } else {
            return getPossibleMovesWhenThereIsNoPendingAction();
        }
    }

    private List<Move> getMovesThatSolvePendingAction() {
        final List<Move> possibleMoves = new ArrayList<>();
        switch (getDiscardPile().getLastPlayed().getType()) {
            case WILD:
                addMovesThatSolvePendingActionFromWildCard(possibleMoves);
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
        return possibleMoves;
    }

    private void addMovesThatSolvePendingActionFromWildCard(List<Move> possibleMoves) {
        for (CardColor possibleColorChoice : CardColor.values()) {
            final List<Integer> indexesOfCardsCompatibleWithColor = getIndexesOfCardsCompatibleWithTestCardOrColor(
                    getPlayers().getCurrentPlayer().getCards(), null, possibleColorChoice);
            if (!indexesOfCardsCompatibleWithColor.isEmpty()) {
                addChooseColorAndPlayCardAtHandMove(possibleMoves, possibleColorChoice,
                        indexesOfCardsCompatibleWithColor);
            } else {
                possibleMoves.add(new Move(new ChooseColor(possibleColorChoice), new DrawCard(1)));
            }

            Card nextOnPile = getDrawPile().getNext();
            if (nextOnPile != null && isCardValidAsNextMove(nextOnPile)) {
                addChooseColorDrawCardAndPlayItMove(possibleMoves, possibleColorChoice, nextOnPile);
            }
        }
    }

    private void addChooseColorAndPlayCardAtHandMove(List<Move> possibleMoves, CardColor possibleColorChoice,
                                                     List<Integer> indexesOfCardsCompatibleWithColor) {
        for (int cardIndex : indexesOfCardsCompatibleWithColor) {
            Card possibleCard = getPlayers().getCurrentPlayer().getCards().get(cardIndex);
            if (possibleCard.isWildCard()) {
                for (CardColor nextColor : CardColor.values()) {
                    possibleMoves.add(new Move(new ChooseColor(possibleColorChoice),
                            new PlayCard(cardIndex, possibleCard), new ChooseColor(nextColor)));
                }
            } else {
                possibleMoves.add(new Move(new ChooseColor(possibleColorChoice),
                        new PlayCard(cardIndex, possibleCard)));
            }
        }
    }

    private void addChooseColorDrawCardAndPlayItMove(List<Move> possibleMoves, CardColor possibleColorChoice,
                                                     Card nextOnPile) {
        if (nextOnPile.isWildCard()) {
            for (CardColor nextColor : CardColor.values()) {
                possibleMoves.add(new Move(new ChooseColor(possibleColorChoice), new DrawCard(1),
                        new PlayCard(-getPlayers().getCurrentPlayer().getCards().size(),
                                nextOnPile), new ChooseColor(nextColor)));
            }
        } else {
            possibleMoves.add(new Move(new ChooseColor(possibleColorChoice), new DrawCard(1),
                    new PlayCard(-getPlayers().getCurrentPlayer().getCards().size(),
                            nextOnPile)));
        }
    }

    private List<Move> getPossibleMovesWhenThereIsNoPendingAction() {
        final List<Move> possibleMoves = new ArrayList<>();
        Card nextOnPile = getDrawPile().getNext();
        if (nextOnPile != null && isCardValidAsNextMove(nextOnPile)) {
            addPlayNextOnPileToPossibleMoves(possibleMoves, nextOnPile);
        }
        List<Integer> currentPlayerPlayableCards = getIndexesOfCardsCompatibleWithTestCardOrColor(
                getPlayers().getCurrentPlayer().getCards(), getDiscardPile().getLastPlayed(),
                getDiscardPile().getLastColor());
        if (!currentPlayerPlayableCards.isEmpty()) {
            addPlayableCardsToPossibleMoves(possibleMoves, currentPlayerPlayableCards);
        } else {
            possibleMoves.add(new Move(new DrawCard(1)));
        }
        return possibleMoves;
    }

    private boolean isCardValidAsNextMove(Card card) {
        Player currentPlayer = getPlayers().getCurrentPlayer();
        Card lastPlayed = getDiscardPile().getLastPlayed();
        CardColor lastColor = getDiscardPile().getLastColor();

        currentPlayer.getCards().add(card);
        int lastWithdrawalIndex = currentPlayer.getCards().size() - 1;
        List<Integer> indexesOfCompatibleCards = getIndexesOfCardsCompatibleWithTestCardOrColor(
                currentPlayer.getCards(), lastPlayed, lastColor);
        boolean validCard = !indexesOfCompatibleCards.isEmpty() && indexesOfCompatibleCards
                .get(indexesOfCompatibleCards.size() - 1).equals(lastWithdrawalIndex);
        currentPlayer.getCards().remove(lastWithdrawalIndex);

        return validCard;
    }

    private void addPlayNextOnPileToPossibleMoves(List<Move> possibleMoves, Card nextOnPile) {
        if (nextOnPile.isWildCard()) {
            for (CardColor nextColor : CardColor.values()) {
                possibleMoves.add(new Move(new DrawCard(1),
                        new PlayCard(-getPlayers().getCurrentPlayer().getCards().size(), nextOnPile),
                        new ChooseColor(nextColor)));
            }
        } else {
            possibleMoves.add(new Move(new DrawCard(1),
                    new PlayCard(-getPlayers().getCurrentPlayer().getCards().size(), nextOnPile)));
        }
    }

    private void addPlayableCardsToPossibleMoves(List<Move> possibleMoves, List<Integer> currentPlayerPlayableCards) {
        for (int playableCardIndex : currentPlayerPlayableCards) {
            Card playableCard = getPlayers().getCurrentPlayer().getCards().get(playableCardIndex);
            if (playableCard.isWildCard()) {
                for (CardColor color : CardColor.values()) {
                    possibleMoves.add(new Move(new PlayCard(playableCardIndex, playableCard),
                            new ChooseColor(color)));
                }
            } else {
                possibleMoves.add(new Move(new PlayCard(playableCardIndex, playableCard)));
            }
        }
    }
}
