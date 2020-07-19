package com.kalaha.api.model;

public class Player {
    private int position;
    private Board.Side side;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Board.Side getSide() {
        return side;
    }

    public void setSide(Board.Side side) {
        this.side = side;
    }
}
