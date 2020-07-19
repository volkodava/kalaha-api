package com.kalaha.api.core;

import com.kalaha.api.exception.UnprocessedOperationException;
import com.kalaha.api.model.Board;
import com.kalaha.api.model.Cell;
import com.kalaha.api.model.Game;
import com.kalaha.api.model.Player;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GameValidator {
    private static final int MIN_PIT_SIZE = 3;
    private static final int EXPECTED_PLAYERS_NUM = 2;

    public void validateGameConfiguration(int pitSizePerSide, int stoneSizePerPit, Player[] players) {
        if (players.length != EXPECTED_PLAYERS_NUM) {
            throw new UnprocessedOperationException(String.format("Expected %s players", EXPECTED_PLAYERS_NUM));
        }

        // check for negative values or 0
        if (pitSizePerSide < MIN_PIT_SIZE) {
            throw new UnprocessedOperationException(String.format("Pit size must have at least %s pits", MIN_PIT_SIZE));
        }
        if (stoneSizePerPit <= 0) {
            throw new UnprocessedOperationException("Stone size must be a positive number");
        }
    }

    public void validateGameState(Game game, int pitId) {
        Board board = game.getBoard();
        Map<Integer, Cell> grid = board.getGrid();
        if (!grid.containsKey(pitId)) {
            throw new UnprocessedOperationException("Pit id NOT valid");
        }

        Player player = game.getPlayer();
        if (!grid.get(pitId).canMove(player)) {
            throw new UnprocessedOperationException("Can't perform move for selected pit. Please select pit on the player's side");
        }

        Game.State state = game.getState();
        if (state != Game.State.RUNNING) {
            throw new UnprocessedOperationException(String.format("Game in %s state, can't perform move", state));
        }
    }
}
