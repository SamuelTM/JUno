package stm.juno;

import stm.juno.moves.Move;
import stm.juno.cards.Card;
import stm.juno.cards.CardType;
import stm.juno.entities.Players;
import stm.juno.moves.MoveFinder;
import stm.juno.piles.DiscardPile;
import stm.juno.piles.DrawPile;
import stm.juno.search.SearchAlg;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

    private final MoveFinder moveFinder;

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

        this.moveFinder = new MoveFinder(this);
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
        moveFinder = new MoveFinder(this);
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
        List<Move> possibleMoves = moveFinder.getPossibleMoves();
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

    public MoveFinder getMoveFinder() {
        return moveFinder;
    }
}
