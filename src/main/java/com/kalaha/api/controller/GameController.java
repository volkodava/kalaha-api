package com.kalaha.api.controller;

import com.kalaha.api.dto.GameResponse;
import com.kalaha.api.dto.MoveResponse;
import com.kalaha.api.dto.NewGameResponse;
import com.kalaha.api.model.Game;
import com.kalaha.api.service.GameService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/games")
public class GameController {

    private final GameService<String, Game> gameService;

    public GameController(GameService<String, Game> gameService) {
        this.gameService = gameService;
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NewGameResponse> newGame(UriComponentsBuilder uriBuilder) {
        String gameId = gameService.newGame();
        URI uri = uriBuilder
                .path("/games/{id}")
                .buildAndExpand(gameId)
                .toUri();
        return ResponseEntity.created(uri)
                .body(new NewGameResponse(gameId, uri.toString()));
    }

    @GetMapping(path = "/{gameId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GameResponse> fetchGameById(@PathVariable(name = "gameId") String gameId) {
        Game game = gameService.findById(gameId);
        return ResponseEntity.ok(GameResponse.builder()
                .withId(gameId)
                .withPlayer(game.getPlayer().getSide().name())
                .withStatus(game.getBoard().getDetailStatus())
                .withPitSizePerSide(game.getBoard().getPitSizePerSide())
                .withStoneSizePerPit(game.getBoard().getStoneSizePerPit())
                .build());
    }

    @PutMapping(path = "/{gameId}/pits/{pitId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MoveResponse> move(@PathVariable(name = "gameId") String gameId,
                                             @PathVariable(name = "pitId") int pitId,
                                             UriComponentsBuilder uriBuilder) {
        Game game = gameService.move(gameId, pitId);
        URI uri = uriBuilder
                .path("/games/{id}")
                .buildAndExpand(gameId)
                .toUri();
        return ResponseEntity.ok(new MoveResponse(gameId, uri.toString(), game.getBoard().getStatus()));
    }
}
