package com.kalaha.api.model;

public class Pit extends Cell {

    @Override
    public boolean canMove(Player player) {
        return stones > 0
                && side == player.getSide();
    }

    @Override
    public boolean isOwnHouse(Player player) {
        return false;
    }

    @Override
    public boolean isPit() {
        return true;
    }

    @Override
    public boolean isOwnPit(Player player) {
        return side == player.getSide();
    }

    @Override
    public int takeAllStones() {
        int currentStones = stones;
        stones = 0;
        return currentStones;
    }

    @Override
    public boolean canAddStones(Player player) {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "P(" + getStones() + ")";
    }
}
