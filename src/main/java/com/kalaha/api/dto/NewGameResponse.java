package com.kalaha.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewGameResponse {
    private final String id;
    private final String uri;

    @JsonCreator
    public NewGameResponse(@JsonProperty("id") String id,
                           @JsonProperty("uri") String uri) {
        this.id = id;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", uri='" + uri + '\'';
    }
}
