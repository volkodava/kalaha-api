package com.kalaha.api.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pit.class, name = "pit"),
        @JsonSubTypes.Type(value = House.class, name = "house")
})
public abstract class Cell {
    protected int position;
    protected Board.Side side;
    protected int stones;

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

    public int getStones() {
        return stones;
    }

    public void setStones(int stones) {
        this.stones = stones;
    }

    public abstract boolean canMove(Player player);

    public void addStones(int newStones) {
        stones += newStones;
    }

    public abstract boolean isOwnHouse(Player player);

    public abstract boolean isPit();

    public abstract boolean isOwnPit(Player player);

    public abstract int takeAllStones();

    public abstract boolean canAddStones(Player player);

    @Override
    public String toString() {
        String sideStr = side == Board.Side.SOUTH ? "S" : "N";
        return position + sideStr;
    }
}
