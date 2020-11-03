package stm.juno.search;

import stm.juno.Uno;
import stm.juno.moves.Move;

import java.util.Arrays;
import java.util.List;

public class SearchAlg {

    public static int maxSearchDepth = 25;

    private static double[] subtractAverage(double[] scores) {
        double average = 0;
        for (double score : scores) {
            average += score;
        }
        average /= scores.length;

        double[] newScores = new double[scores.length];
        for (int i = 0; i < scores.length; i++) {
            newScores[i] = scores[i] - average;
        }

        return newScores;
    }

    public static Tuple<Move, double[]> NMax(Uno game, int depth) {
        depth++;

        if (game.getWinnerIndex() != -1 || depth == maxSearchDepth) {
            return new Tuple<>(new Move(), subtractAverage(game.getScores()));
        } else {
            Tuple<Move, double[]> bestMove = null;
            double alpha = -Double.MAX_VALUE;
            List<Move> possibleMoves = game.getMoveFinder().getPossibleMoves();
            for (Move move : possibleMoves) {
                Uno clone = new Uno(game);
                clone.executeMove(move, false);

                int p =  game.getPlayers().getCurrentPlayerIndex();
                Tuple<Move, double[]> psiStar = NMax(clone, depth);

                if (alpha < psiStar.y[p]) {
                    alpha = psiStar.y[p];
                    bestMove = new Tuple<>(move, psiStar.y);
                }
            }

            return bestMove;
        }
    }

    private static double sum(double[] values) {
        double sum = 0;
        for(double value: values) {
            sum += value;
        }

        return sum;
    }

    public static double[] getAlpha(int size) {
        double[] alpha = new double[size];
        for (int i = 0; i < size; i++) {
            alpha[i] = Double.MIN_VALUE;
        }

        return alpha;
    }

    public static Tuple<Move, double[]> hypermax(Uno game, int depth, double[] alpha) {
        depth++;
        if (game.getWinnerIndex() != -1 || depth == maxSearchDepth) {
            return new Tuple<>(new Move(), subtractAverage(game.getScores()));
        } else {
            Tuple<Move, double[]> bestMove = null;
            List<Move> possibleMoves = game.getMoveFinder().getPossibleMoves();
            for (int i = 0; i < possibleMoves.size(); i++) {
                Uno clone = new Uno(game);
                clone.executeMove(possibleMoves.get(i), false);
                int currentPlayerIndex = game.getPlayers().getCurrentPlayerIndex();

                Tuple<Move, double[]> psiStar = hypermax(clone, depth, Arrays.copyOf(alpha, alpha.length));

                if (i == 0) {
                    bestMove = new Tuple<>(possibleMoves.get(i), psiStar.y);
                }

                if (alpha[currentPlayerIndex] < psiStar.y[currentPlayerIndex]) {
                    alpha[currentPlayerIndex] = psiStar.y[currentPlayerIndex];
                    bestMove = new Tuple<>(possibleMoves.get(i), psiStar.y);
                }

                if (sum(alpha) >= 0) {
                    break;
                }
            }

            return bestMove;
        }
    }
}


