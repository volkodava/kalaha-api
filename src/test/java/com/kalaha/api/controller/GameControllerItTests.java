package com.kalaha.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaha.api.BaseItTests;
import com.kalaha.api.dto.NewGameResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class GameControllerItTests extends BaseItTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void playerCanCreateNewGame() throws Exception {
        mockMvc.perform(post("/games"))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.uri").isNotEmpty());
    }

    @Test
    public void playerCanFetchGameById() throws Exception {
        String gameId = createGame();

        mockMvc.perform(get("/games/{gameId}", gameId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.player").isNotEmpty())
                .andExpect(jsonPath("$.status").isMap());
    }

    @Test
    public void fetchNonExistingGameByIdShouldReturnError() throws Exception {
        String gameId = "NOT_EXISTING_GAME";

        mockMvc.perform(get("/games/{gameId}", gameId))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    public void playerCanMove() throws Exception {
        String gameId = createGame();
        String pitId = "1";

        mockMvc.perform(put("/games/{gameId}/pits/{pitId}", gameId, pitId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.url").isNotEmpty())
                .andExpect(jsonPath("$.status").isMap());
    }

    @Test
    public void performMoveForNonExistingGameShouldReturnError() throws Exception {
        String gameId = "NOT_EXISTING_GAME";
        String pitId = "1";

        mockMvc.perform(put("/games/{gameId}/pits/{pitId}", gameId, pitId))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    public void moveOnOpponentPitShouldReturnError() throws Exception {
        String gameId = createGame();
        String pitId = "7";

        mockMvc.perform(put("/games/{gameId}/pits/{pitId}", gameId, pitId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    private String createGame() throws Exception {
        String jsonResponse = mockMvc.perform(post("/games"))
                .andReturn()
                .getResponse().getContentAsString();
        NewGameResponse newGameResponse = mapper.readValue(jsonResponse, NewGameResponse.class);
        return newGameResponse.getId();
    }
}
