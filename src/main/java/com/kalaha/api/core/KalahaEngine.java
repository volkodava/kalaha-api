package com.kalaha.api.core;

import com.kalaha.api.model.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KalahaEngine {
    private final GameValidator gameValidator;

    public KalahaEngine(GameValidator gameValidator) {
        this.gameValidator = gameValidator;
    }

    public Game newGame(int pitSizePerSide, int stoneSizePerPit, List<Player> players) {
        gameValidator.validateGameConfiguration(pitSizePerSide, stoneSizePerPit, players);
        return createGame(pitSizePerSide, stoneSizePerPit, players);
    }

    public Game move(Game game, int pitId) {
        gameValidator.validateGameState(game, pitId);
        return executeMove(game, pitId);
    }

    private Game createGame(int pitSizePerSide, int stoneSizePerPit, List<Player> players) {
        // grid = (N pits + 1 house) * 2 (players)
        int gridSize = (pitSizePerSide + 1) * 2;
        int housePosition = pitSizePerSide + 1;
        Map<Integer, Cell> grid = new HashMap<>();
        for (int i = 1; i <= gridSize; i++) {
            Cell cell;
            if (i % housePosition == 0) {
                cell = new House();
                cell.setStones(0);
            } else {
                cell = new Pit();
                cell.setStones(stoneSizePerPit);
            }
            Board.Side side = i <= housePosition ? Board.Side.SOUTH : Board.Side.NORTH;
            cell.setSide(side);
            cell.setPosition(i);

            grid.put(i, cell);
        }

        Board board = new Board();
        board.setPitSizePerSide(pitSizePerSide);
        board.setStoneSizePerPit(stoneSizePerPit);
        board.setGrid(grid);

        Game game = new Game();
        game.setBoard(board);
        game.setPlayers(players);
        game.setPlayerIndex(players.get(0).getPosition());
        game.setWinnerPlayerIndex(-1);
        game.setState(Game.State.RUNNING);

        return game;
    }

    private Game executeMove(Game game, int pitId) {
        Player player = game.getPlayer();
        Board board = game.getBoard();
        Map<Integer, Cell> grid = board.getGrid();
        Cell cell = grid.get(pitId);
        int stones = cell.getStones();
        cell.setStones(0);

        int cellPosition = pitId + 1;
        Cell prevCell = null;
        Cell curCell = grid.get(cellPosition);
        while (stones > 0) {
            if (curCell.canAddStones(player)) {
                curCell.addStones(1);
                stones--;
            }

            cellPosition++;
            if (cellPosition == grid.size() + 1) {
                cellPosition = 1;
            }

            prevCell = curCell;
            curCell = grid.get(cellPosition);
        }

        Cell finalCell = prevCell;
        if (finalCell.getStones() == 1
                && finalCell.isOwnPit(player)) {
            board.collectCurrentAndOppositeStones(player, finalCell);
        }

        if (board.sideIsEmpty(player)) {
            board.collectSideStones(game.getNextPlayer());
            game.complete();
        } else if (!finalCell.isOwnHouse(player)) {
            game.setNextPlayer();
        }

        return game;
    }
}
