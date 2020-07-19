package com.kalaha.api.model;

public class Game {
    private String id;
    private Board board;
    private Player[] players;
    private int playerIndex;
    private int winnerPlayerIndex;
    private State state;

    public enum State {
        RUNNING, DRAW, WIN
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public int getWinnerPlayerIndex() {
        return winnerPlayerIndex;
    }

    public void setWinnerPlayerIndex(int winnerPlayerIndex) {
        this.winnerPlayerIndex = winnerPlayerIndex;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Player getPlayer() {
        return players[playerIndex];
    }

    public Player getNextPlayer() {
        return players[getNextPlayerIndex()];
    }

    public void setNextPlayer() {
        playerIndex = getNextPlayerIndex();
    }

    public int getNextPlayerIndex() {
        return playerIndex == 0 ? 1 : 0;
    }

    public void complete() {
        Player curPlayer = getPlayer();
        Player nextPlayer = getNextPlayer();
        int curPlayerSum = board.sumHouseStones(curPlayer);
        int nextPlayerSum = board.sumHouseStones(nextPlayer);

        if (curPlayerSum == nextPlayerSum) {
            state = State.DRAW;
        } else if (curPlayerSum > nextPlayerSum) {
            state = State.WIN;
            winnerPlayerIndex = curPlayer.getPosition();
        } else {
            state = State.WIN;
            winnerPlayerIndex = nextPlayer.getPosition();
        }
    }
}
