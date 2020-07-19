package com.kalaha.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class MoveResponse {
    private final String id;
    private final String url;
    private final Map<String, String> status;

    @JsonCreator
    public MoveResponse(@JsonProperty("id") String id,
                        @JsonProperty("url") String url,
                        @JsonProperty("status") Map<String, String> status) {
        this.id = id;
        this.url = url;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", status=" + status;
    }
}
