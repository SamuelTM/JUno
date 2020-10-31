package stm.juno.moves;

import stm.juno.Uno;
import stm.juno.actions.*;
import stm.juno.cards.Card;
import stm.juno.cards.CardColor;
import stm.juno.cards.CardType;
import stm.juno.entities.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoveFinder {

    private final Uno game;

    public MoveFinder(Uno game) {
        this.game = game;
    }

    private List<Integer> getIndexesOfCardsCompatibleWithTestCardOrTestColor(List<Card> cards, Card testCard,
                                                                             CardColor testColor) {
        final List<Integer> indexesOfCompatibleCards = new ArrayList<>();
        final HashMap<Card, Integer> uniqueCards = new HashMap<>();
        for (int i = 0; i < cards.size(); i++) {
            Card currentPlayerCard = cards.get(i);
            if (currentPlayerCard.isWildCard()) {
                uniqueCards.putIfAbsent(currentPlayerCard, i);
            } else {
                if (testCard != null && testCard.isWildCard()) {
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

        for (Card card : uniqueCards.keySet()) {
            indexesOfCompatibleCards.add(uniqueCards.get(card));
        }

        return indexesOfCompatibleCards;
    }

    public List<Move> getPossibleMoves() {
        if (game.getDiscardPile().isPendingAction()) {
            return getMoveThatSolvesPendingAction();
        } else {
            return getPossibleMovesWhenThereIsNoPendingAction();
        }
    }

    private List<Move> getMoveThatSolvesPendingAction() {
        final List<Move> possibleMoves = new ArrayList<>();
        switch (game.getDiscardPile().getLastPlayed().getType()) {
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
            final List<Integer> indexesOfCardsCompatibleWithColor = getIndexesOfCardsCompatibleWithTestCardOrTestColor(
                    game.getPlayers().getCurrentPlayer().getCards(), null, possibleColorChoice);
            if (!indexesOfCardsCompatibleWithColor.isEmpty()) {
                addChooseColorAndPlayCardAtHandMove(possibleMoves, possibleColorChoice,
                        indexesOfCardsCompatibleWithColor);
            } else {
                // If we don't have a suitable card, we choose the color and draw one from the pile
                possibleMoves.add(new Move(new ChooseColor(possibleColorChoice), new DrawCard(1)));
            }

            Card nextOnPile = game.getDrawPile().getNext();
            if (isCardValidAsNextMove(nextOnPile)) {
                addChooseColorDrawCardAndPlayItMove(possibleMoves, possibleColorChoice, nextOnPile);

                // Technically, we could also add the possibility of choosing a color, drawing a card
                // and keeping it. However, this would generate a problematic tree of possibilities
                // since each player could choose to draw a card and keep it instead of playing it, so
                // no one would ever play.

            }
        }
    }

    private void addChooseColorAndPlayCardAtHandMove(List<Move> possibleMoves, CardColor possibleColorChoice,
                                                     List<Integer> indexesOfCardsCompatibleWithColor) {
        for (int cardIndex : indexesOfCardsCompatibleWithColor) {
            Card possibleCard = game.getPlayers().getCurrentPlayer().getCards().get(cardIndex);
            if (possibleCard.isWildCard()) {
                for (CardColor nextColor : CardColor.values()) {
                    // We can choose the color, play a wild or wild draw four and choose the color
                    possibleMoves.add(new Move(new ChooseColor(possibleColorChoice),
                            new PlayCard(cardIndex, possibleCard), new ChooseColor(nextColor)));
                }
            } else {
                // Or we can choose the color and play any other card that isn't a wild
                possibleMoves.add(new Move(new ChooseColor(possibleColorChoice),
                        new PlayCard(cardIndex, possibleCard)));
            }
        }
    }

    private void addChooseColorDrawCardAndPlayItMove(List<Move> possibleMoves, CardColor possibleColorChoice,
                                                     Card nextOnPile) {
        if (nextOnPile.isWildCard()) {
            for (CardColor nextColor : CardColor.values()) {
                // We can also choose the color, draw a card that is a wild or wild draw four,
                // play it and choose the color
                possibleMoves.add(new Move(new ChooseColor(possibleColorChoice), new DrawCard(1),
                        new PlayCard(-game.getPlayers().getCurrentPlayer().getCards().size(),
                                nextOnPile), new ChooseColor(nextColor)));
            }
        } else {
            // And we can choose the color, draw a card that isn't a wild but can be played
            // and play it
            possibleMoves.add(new Move(new ChooseColor(possibleColorChoice), new DrawCard(1),
                    new PlayCard(-game.getPlayers().getCurrentPlayer().getCards().size(),
                            nextOnPile)));
        }
    }

    private List<Move> getPossibleMovesWhenThereIsNoPendingAction() {
        final List<Move> possibleMoves = new ArrayList<>();
        Card nextOnPile = game.getDrawPile().getNext();
        if (isCardValidAsNextMove(nextOnPile)) {
            addPlayNextOnPileToPossibleMoves(possibleMoves, nextOnPile);
        }
        List<Integer> currentPlayersPlayableCards = getIndexesOfCardsCompatibleWithTestCardOrTestColor(
                game.getPlayers().getCurrentPlayer().getCards(), game.getDiscardPile().getLastPlayed(),
                game.getDiscardPile().getLastColor());
        if (!currentPlayersPlayableCards.isEmpty()) {
            addPlayableCardsToPossibleMoves(possibleMoves, currentPlayersPlayableCards);
        } else {
            // If we don't have a suitable card to play, we're going to need to draw one from the pile
            possibleMoves.add(new Move(new DrawCard(1)));
        }

        // Technically, we could also add the possibility of drawing a card and keeping it.
        // However, this would generate a problematic tree of possibilities since each player
        // could choose to draw a card and keep it instead of playing it, so no one would ever
        // play.

        return possibleMoves;
    }

    private boolean isCardValidAsNextMove(Card card) {
        if (card != null) {
            Player currentPlayer = game.getPlayers().getCurrentPlayer();
            Card lastPlayed = game.getDiscardPile().getLastPlayed();
            CardColor lastColor = game.getDiscardPile().getLastColor();

            currentPlayer.getCards().add(card);
            int lastWithdrawalIndex = currentPlayer.getCards().size() - 1;
            List<Integer> indexesOfCompatibleCards = getIndexesOfCardsCompatibleWithTestCardOrTestColor(
                    currentPlayer.getCards(), lastPlayed, lastColor);
            boolean validCard = !indexesOfCompatibleCards.isEmpty() && indexesOfCompatibleCards
                    .get(indexesOfCompatibleCards.size() - 1).equals(lastWithdrawalIndex);
            currentPlayer.getCards().remove(lastWithdrawalIndex);

            return validCard;
        }
        return false;
    }

    private void addPlayNextOnPileToPossibleMoves(List<Move> possibleMoves, Card nextOnPile) {
        if (nextOnPile.isWildCard()) {
            for (CardColor nextColor : CardColor.values()) {
                // We can draw a wild or wild draw four, play it and choose the color
                possibleMoves.add(new Move(new DrawCard(1),
                        new PlayCard(-game.getPlayers().getCurrentPlayer().getCards().size(), nextOnPile),
                        new ChooseColor(nextColor)));
            }
        } else {
            // We can draw a card that isn't a wild but can be played, then play it
            possibleMoves.add(new Move(new DrawCard(1),
                    new PlayCard(-game.getPlayers().getCurrentPlayer().getCards().size(), nextOnPile)));
        }
    }

    private void addPlayableCardsToPossibleMoves(List<Move> possibleMoves, List<Integer> currentPlayerPlayableCards) {
        for (int playableCardIndex : currentPlayerPlayableCards) {
            Card playableCard = game.getPlayers().getCurrentPlayer().getCards().get(playableCardIndex);
            if (playableCard.isWildCard()) {
                for (CardColor color : CardColor.values()) {
                    // We can play a wild or wild draw four and choose the color
                    possibleMoves.add(new Move(new PlayCard(playableCardIndex, playableCard),
                            new ChooseColor(color)));
                }
            } else {
                // Or we can just play any other available card
                possibleMoves.add(new Move(new PlayCard(playableCardIndex, playableCard)));
            }
        }
    }
}
