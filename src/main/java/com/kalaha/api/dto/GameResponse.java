package com.kalaha.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class GameResponse {
    private final String id;
    private final String player;
    private final Map<String, String> status;
    private final int pitSizePerSide;
    private final int stoneSizePerPit;

    @JsonCreator
    public GameResponse(@JsonProperty("id") String id,
                        @JsonProperty("player") String player,
                        @JsonProperty("status") Map<String, String> status,
                        @JsonProperty("pitSizePerSide") int pitSizePerSide,
                        @JsonProperty("stoneSizePerPit") int stoneSizePerPit) {
        this.id = id;
        this.player = player;
        this.status = status;
        this.pitSizePerSide = pitSizePerSide;
        this.stoneSizePerPit = stoneSizePerPit;
    }

    public String getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public int getPitSizePerSide() {
        return pitSizePerSide;
    }

    public int getStoneSizePerPit() {
        return stoneSizePerPit;
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", player='" + player + '\'' +
                ", status=" + status +
                ", pitSizePerSide=" + pitSizePerSide +
                ", stoneSizePerPit=" + stoneSizePerPit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String player;
        private Map<String, String> status;
        private int pitSizePerSide;
        private int stoneSizePerPit;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withPlayer(String player) {
            this.player = player;
            return this;
        }

        public Builder withStatus(Map<String, String> status) {
            this.status = status;
            return this;
        }

        public Builder withPitSizePerSide(int pitSizePerSide) {
            this.pitSizePerSide = pitSizePerSide;
            return this;
        }

        public Builder withStoneSizePerPit(int stoneSizePerPit) {
            this.stoneSizePerPit = stoneSizePerPit;
            return this;
        }

        public GameResponse build() {
            return new GameResponse(id, player, status, pitSizePerSide, stoneSizePerPit);
        }
    }
}
