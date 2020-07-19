package com.kalaha.api.model;

import com.kalaha.api.exception.UnprocessedOperationException;

public class House extends Cell {

    @Override
    public boolean canMove(Player player) {
        return false;
    }

    @Override
    public boolean isOwnHouse(Player player) {
        return side == player.getSide();
    }

    @Override
    public boolean isPit() {
        return false;
    }

    @Override
    public boolean isOwnPit(Player player) {
        return false;
    }

    @Override
    public int takeAllStones() {
        throw new UnprocessedOperationException("Can't take stones from the house");
    }

    @Override
    public boolean canAddStones(Player player) {
        return side == player.getSide();
    }

    @Override
    public String toString() {
        return super.toString() + "H(" + getStones() + ")";
    }
}
