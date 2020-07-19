package com.kalaha.api.core;

import com.kalaha.api.exception.UnprocessedOperationException;
import com.kalaha.api.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KalahaEngineTests {
    private final int pitSize;
    private final int stoneSize;
    private final Player southPlayer;
    private final Player northPlayer;
    private final List<Player> players;
    private final int playerSouthStart;
    private final int playerSouthHouse;
    private final int playerNorthStart;
    private final int playerNorthHouse;
    private final KalahaEngine kalahaEngine;

    public KalahaEngineTests() {
        this.pitSize = 6;
        this.stoneSize = 6;
        this.southPlayer = new Player();
        this.southPlayer.setSide(Board.Side.SOUTH);
        this.southPlayer.setPosition(0);
        this.northPlayer = new Player();
        this.northPlayer.setSide(Board.Side.NORTH);
        this.northPlayer.setPosition(1);
        this.players = List.of(this.southPlayer, this.northPlayer);
        this.playerSouthStart = 1;
        this.playerSouthHouse = 7;
        this.playerNorthStart = 8;
        this.playerNorthHouse = 14;
        this.kalahaEngine = new KalahaEngine(new GameValidator());
    }

    @Test
    public void playerCanCreateNewGame() {
        // when
        Game game = kalahaEngine.newGame(pitSize, stoneSize, players);

        // then
        assertThat(game).isNotNull();
        assertThat(game.getBoard()).isNotNull();
        assertThat(game.getPlayers()).hasSize(players.size());
        assertThat(game.getPlayerIndex()).isEqualTo(southPlayer.getPosition());
        assertThat(game.getPlayer()).isEqualTo(southPlayer);
        assertThat(game.getNextPlayerIndex()).isEqualTo(northPlayer.getPosition());
        assertThat(game.getNextPlayer()).isEqualTo(northPlayer);
        assertThat(game.getState()).isEqualTo(Game.State.RUNNING);
        assertThat(game.getBoard().getGrid()).isNotNull();
    }

    @Test
    public void initialGameBoardStateIsValid() {
        // given
        int EXPECTED_GRID_SIZE = (pitSize + 1) * 2;

        // when
        Game game = kalahaEngine.newGame(pitSize, stoneSize, players);

        // then
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        assertThat(grid).hasSize(EXPECTED_GRID_SIZE);
        assertCellProps(grid.get(1), Pit.class, 1, Board.Side.SOUTH, stoneSize);
        assertCellProps(grid.get(2), Pit.class, 2, Board.Side.SOUTH, stoneSize);
        assertCellProps(grid.get(3), Pit.class, 3, Board.Side.SOUTH, stoneSize);
        assertCellProps(grid.get(4), Pit.class, 4, Board.Side.SOUTH, stoneSize);
        assertCellProps(grid.get(5), Pit.class, 5, Board.Side.SOUTH, stoneSize);
        assertCellProps(grid.get(6), Pit.class, 6, Board.Side.SOUTH, stoneSize);
        assertCellProps(grid.get(7), House.class, 7, Board.Side.SOUTH, 0);
        assertCellProps(grid.get(8), Pit.class, 8, Board.Side.NORTH, stoneSize);
        assertCellProps(grid.get(9), Pit.class, 9, Board.Side.NORTH, stoneSize);
        assertCellProps(grid.get(10), Pit.class, 10, Board.Side.NORTH, stoneSize);
        assertCellProps(grid.get(11), Pit.class, 11, Board.Side.NORTH, stoneSize);
        assertCellProps(grid.get(12), Pit.class, 12, Board.Side.NORTH, stoneSize);
        assertCellProps(grid.get(13), Pit.class, 13, Board.Side.NORTH, stoneSize);
        assertCellProps(grid.get(14), House.class, 14, Board.Side.NORTH, 0);
    }

    @Test
    public void playerCantMoveFromOwnEmptyPit() {
        // given
        Game game = newGameWithEmptyGrid();

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, playerSouthStart));
    }

    @Test
    public void playerCantMoveFromOpponentPit() {
        // given
        Game game = newGameWithEmptyGrid();
        int pitId = playerNorthStart;
        game.getBoard().getGrid().get(pitId).setStones(stoneSize);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, pitId));
    }

    @Test
    public void playerCantMoveFromOwnHouse() {
        // given
        Game game = newGameWithEmptyGrid();
        int pitId = playerSouthHouse;
        game.getBoard().getGrid().get(pitId).setStones(stoneSize);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, pitId));
    }

    @Test
    public void playerCantMoveFromNonExistingCell() {
        // given
        Game game = kalahaEngine.newGame(pitSize, stoneSize, players);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, 0));
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, -1));
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, 15));
    }

    @Test
    public void playerCantMoveIfGameInDrawState() {
        // given
        Game game = kalahaEngine.newGame(pitSize, stoneSize, players);
        game.setState(Game.State.DRAW);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, playerSouthStart));
    }

    @Test
    public void playerCantMoveIfGameInWinState() {
        // given
        Game game = kalahaEngine.newGame(pitSize, stoneSize, players);
        game.setState(Game.State.WIN);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        assertThrows(UnprocessedOperationException.class,
                () -> kalahaEngine.move(game, playerSouthStart));
    }

    @Test
    public void anotherTurnShouldBeAvailableForThePlayerWhichLastTurnEndsInOwnHouse() {
        // given
        Game game = newGameWithEmptyGrid();
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        int pitId = playerSouthHouse - 1; // 6SP move position
        grid.get(pitId).setStones(1);
        grid.get(playerSouthStart).setStones(1);
        grid.get(playerNorthStart).setStones(1);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        kalahaEngine.move(game, pitId);

        // then
        assertThat(game.getPlayer()).isEqualTo(southPlayer);
    }

    @Test
    public void playersTurnSwitchedWhenLastStoneDroppedNotInOwnPlayersHouse() {
        // given
        Game game = newGameWithEmptyGrid();
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        int pitId = playerSouthStart; // 1SP move position
        grid.get(pitId).setStones(1);
        grid.get(pitId + 1).setStones(1);
        grid.get(playerNorthStart).setStones(1);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        kalahaEngine.move(game, pitId);

        // then
        assertThat(game.getPlayer()).isEqualTo(northPlayer);
    }

    @Test
    public void playerCanMoveOverOwnHouse() {
        // given
        Game game = newGameWithEmptyGrid();
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        int pitId = playerSouthHouse - 1; // 6SP move position
        grid.get(pitId - 1).setStones(1);
        grid.get(pitId).setStones(2);
        grid.get(playerSouthHouse).setStones(1);
        grid.get(playerSouthHouse + 1).setStones(1);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        kalahaEngine.move(game, pitId);

        // then
        assertThat(grid.get(pitId - 1).getStones()).isEqualTo(1);
        assertThat(grid.get(pitId).getStones()).isEqualTo(0);
        assertThat(grid.get(playerSouthHouse).getStones()).isEqualTo(2);
        assertThat(grid.get(playerSouthHouse + 1).getStones()).isEqualTo(2);
    }

    @Test
    public void playerShouldSkipOpponentHouse() {
        // given
        Game game = newGameWithEmptyGrid();
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        int pitId = playerSouthHouse - 1; // 6SP move position
        grid.get(playerSouthStart).setStones(1);
        grid.get(pitId).setStones(pitSize + 2);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        kalahaEngine.move(game, pitId);

        // then
        assertThat(grid.get(pitId).getStones()).isEqualTo(0);
        assertThat(grid.get(playerSouthStart).getStones()).isEqualTo(2);
        assertThat(grid.get(playerNorthHouse).getStones()).isEqualTo(0);
    }

    @Test
    public void playerShouldCaptureOpponentStonesWhenLandingInEmptyPit() {
        // given
        Game game = newGameWithEmptyGrid();
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        int pitId = playerSouthStart; // 1SP move position
        grid.get(pitId).setStones(1);
        grid.get(playerNorthStart + 1).setStones(1); // 9NP

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        kalahaEngine.move(game, pitId);

        // then
        assertThat(grid.get(playerSouthHouse).getStones()).isEqualTo(2);
        assertThat(grid.get(pitId).getStones()).isEqualTo(0);
        assertThat(grid.get(pitId + 1).getStones()).isEqualTo(0);
        assertThat(grid.get(playerNorthStart + 1).getStones()).isEqualTo(0);
    }

    @Test
    public void emptyPlayerSideShouldSetGameStateToWinWhenAnyPlayerHasMostStones() {
        // given
        Game game = newGameWithEmptyGrid();
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        int pitId = playerSouthHouse - 1; // 6SP move position
        grid.get(pitId).setStones(1);
        grid.get(playerNorthStart).setStones(2);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        kalahaEngine.move(game, pitId);

        // then
        assertThat(grid.get(playerSouthHouse).getStones()).isEqualTo(1);
        assertThat(grid.get(playerNorthHouse).getStones()).isEqualTo(2);
        for (Cell cell : grid.values()) {
            if (cell.isPit()) {
                assertThat(cell.getStones()).isEqualTo(0);
            }
        }
        assertThat(game.getWinnerPlayerIndex()).isEqualTo(northPlayer.getPosition());
        assertThat(game.getState()).isEqualTo(Game.State.WIN);
    }

    @Test
    public void emptyPlayerSideShouldSetGameStateToWinWhenPlayersHaveSameNumberOfStones() {
        // given
        Game game = newGameWithEmptyGrid();
        Map<Integer, Cell> grid = game.getBoard().getGrid();
        int pitId = playerSouthHouse - 1; // 6SP move position
        grid.get(pitId).setStones(1);
        grid.get(playerNorthStart).setStones(1);

        // when
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house
        kalahaEngine.move(game, pitId);

        // then
        assertThat(grid.get(playerSouthHouse).getStones()).isEqualTo(1);
        assertThat(grid.get(playerNorthHouse).getStones()).isEqualTo(1);
        for (Cell cell : grid.values()) {
            if (cell.isPit()) {
                assertThat(cell.getStones()).isEqualTo(0);
            }
        }
        assertThat(game.getWinnerPlayerIndex()).isEqualTo(-1);
        assertThat(game.getState()).isEqualTo(Game.State.DRAW);
    }

    private <T> void assertCellProps(Cell cell, Class<T> type, int position, Board.Side side, int stones) {
        assertThat(cell).isInstanceOf(type);
        assertThat(cell).extracting(Cell::getPosition).isEqualTo(position);
        assertThat(cell).extracting(Cell::getSide).isEqualTo(side);
        assertThat(cell).extracting(Cell::getStones).isEqualTo(stones);
    }

    private Game newGameWithEmptyGrid() {
        Map<Integer, Cell> emptyGrid = new TreeMap<>(Map.ofEntries(
                Map.entry(1, newCell(1, Board.Side.SOUTH, 0, Pit.class)),
                Map.entry(2, newCell(2, Board.Side.SOUTH, 0, Pit.class)),
                Map.entry(3, newCell(3, Board.Side.SOUTH, 0, Pit.class)),
                Map.entry(4, newCell(4, Board.Side.SOUTH, 0, Pit.class)),
                Map.entry(5, newCell(5, Board.Side.SOUTH, 0, Pit.class)),
                Map.entry(6, newCell(6, Board.Side.SOUTH, 0, Pit.class)),
                Map.entry(7, newCell(7, Board.Side.SOUTH, 0, House.class)),
                Map.entry(8, newCell(8, Board.Side.NORTH, 0, Pit.class)),
                Map.entry(9, newCell(9, Board.Side.NORTH, 0, Pit.class)),
                Map.entry(10, newCell(10, Board.Side.NORTH, 0, Pit.class)),
                Map.entry(11, newCell(11, Board.Side.NORTH, 0, Pit.class)),
                Map.entry(12, newCell(12, Board.Side.NORTH, 0, Pit.class)),
                Map.entry(13, newCell(13, Board.Side.NORTH, 0, Pit.class)),
                Map.entry(14, newCell(14, Board.Side.NORTH, 0, House.class))
        ));
        Board board = new Board();
        board.setPitSizePerSide(pitSize);
        board.setStoneSizePerPit(stoneSize);
        board.setGrid(emptyGrid);

        Game game = new Game();
        game.setBoard(board);
        game.setPlayers(players);
        game.setPlayerIndex(players.get(0).getPosition());
        game.setWinnerPlayerIndex(-1);
        game.setState(Game.State.RUNNING);

        return game;
    }

    private <T> Cell newCell(int position,
                             Board.Side side,
                             int stones,
                             Class<T> cellType) {
        Cell cell;
        if (cellType == Pit.class) {
            cell = new Pit();
        } else if (cellType == House.class) {
            cell = new House();
        } else {
            throw new UnsupportedOperationException(
                    String.format("Cell type '%s' not supported", cellType.getSimpleName()));
        }
        cell.setPosition(position);
        cell.setSide(side);
        cell.setStones(stones);

        return cell;
    }
}
