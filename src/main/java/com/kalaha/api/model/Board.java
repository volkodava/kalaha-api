package com.kalaha.api.model;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board {
    private int pitSizePerSide;
    private int stoneSizePerPit;
    private Map<Integer, Cell> grid;

    public enum Side {
        SOUTH, NORTH
    }

    public int getPitSizePerSide() {
        return pitSizePerSide;
    }

    public void setPitSizePerSide(int pitSizePerSide) {
        this.pitSizePerSide = pitSizePerSide;
    }

    public int getStoneSizePerPit() {
        return stoneSizePerPit;
    }

    public void setStoneSizePerPit(int stoneSizePerPit) {
        this.stoneSizePerPit = stoneSizePerPit;
    }

    public Map<Integer, Cell> getGrid() {
        return grid;
    }

    public void setGrid(Map<Integer, Cell> grid) {
        this.grid = grid;
    }

    public Map<String, String> getStatus() {
        return grid.entrySet().stream()
                .map(entry -> Map.entry(
                        String.valueOf(entry.getKey()),
                        String.valueOf(entry.getValue().getStones())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v2,
                        () -> new TreeMap<>(Comparator.comparing(Integer::parseInt))));
    }

    public Map<String, String> getDetailStatus() {
        return grid.entrySet().stream()
                .map(entry -> Map.entry(
                        String.valueOf(entry.getKey()),
                        getCellStatusAndSide(entry.getValue())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v2,
                        () -> new TreeMap<>(Comparator.comparing(Integer::parseInt))));
    }

    public Cell getOppositeCell(Cell cell) {
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house

        int endSidePosition = pitSizePerSide + 1;
        int oppositeCellPosition;
        if (cell.getPosition() <= endSidePosition) {
            oppositeCellPosition = cell.getPosition() + endSidePosition;
        } else {
            oppositeCellPosition = cell.getPosition() - endSidePosition;
        }

        return grid.get(oppositeCellPosition);
    }

    public void collectCurrentAndOppositeStones(Player player, Cell cell) {
        // there is always just one stone in the current cell
        int capturedStones = cell.takeAllStones();
        Cell oppositeCell = getOppositeCell(cell);
        capturedStones += oppositeCell.takeAllStones();
        addToHouse(player, capturedStones);
    }

    public boolean sideIsEmpty(Player player) {
        int stones = getPitStream(player)
                .map(Cell::getStones)
                .reduce(Integer::sum)
                .orElse(0);
        return stones == 0;
    }

    public void collectSideStones(Player player) {
        int stones = getPitStream(player)
                .map(Cell::takeAllStones)
                .reduce(Integer::sum)
                .orElse(0);

        Cell house = getHouse(player);
        house.addStones(stones);
    }

    private Cell getHouse(Player player) {
        // 1SP  2SP   3SP   4SP   5SP   6SP   7SH
        // 8NP  9NP  10NP  11NP  12NP  13NP  14NH
        // ------
        // where,
        // SP - south pit
        // NP - north pit
        // SH - south house
        // NH - north house

        if (player.getSide() == Side.SOUTH) {
            return grid.get(pitSizePerSide + 1);
        } else {
            return grid.get(grid.size());
        }
    }

    public int sumHouseStones(Player player) {
        return getHouse(player).getStones();
    }

    public void addToHouse(Player player, int stones) {
        getHouse(player).addStones(stones);
    }

    private String getCellStatusAndSide(Cell cell) {
        String cellType;
        if (cell.isPit()) {
            cellType = "Pit";
        } else {
            cellType = "House";
        }
        return String.format("%s %s (%s)", cell.getSide(), cellType, cell.getStones());
    }

    private Stream<Cell> getPitStream(Player player) {
        // start - inclusive
        // end - exclusive
        int southSideStart = 1;
        int northSideStart = pitSizePerSide + 2;

        int startIncl;
        int endExcl;
        if (player.getSide() == Side.SOUTH) {
            startIncl = southSideStart;
            endExcl = northSideStart;
        } else {
            startIncl = northSideStart;
            endExcl = grid.size() + 1;
        }

        return IntStream.range(startIncl, endExcl)
                .mapToObj(grid::get)
                .filter(Cell::isPit);
    }
}
