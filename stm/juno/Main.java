package stm.juno;

import stm.juno.search.SearchAlg;

import java.util.HashMap;

public class Main {

    private static void playGames(int nGames, int nPlayers, boolean verbose) {
        HashMap<Integer, Integer> victories = new HashMap<>();
        for (int i = 0; i < nGames; i++) {
            Uno uno = new Uno(nPlayers, verbose);
            while (true) {
                if (!uno.nextMove()) {
                    break;
                }
            }
            int winner = uno.getWinnerIndex();
            if (victories.containsKey(winner)) {
                victories.put(winner, victories.get(winner) + 1);
            } else {
                victories.put(winner, 1);
            }
            System.out.println(i);
        }
        System.out.println(victories);
    }

    public static void main(String[] args) {
        SearchAlg.maxSearchDepth = 25;
        playGames(1, 10, true);
    }
}
