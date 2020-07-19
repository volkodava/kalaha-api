package com.kalaha.api.service;

import com.kalaha.api.core.KalahaEngine;
import com.kalaha.api.exception.NotFoundException;
import com.kalaha.api.model.Board;
import com.kalaha.api.model.Game;
import com.kalaha.api.model.Player;
import com.kalaha.api.repository.GameRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameRedisService implements GameService {

    @Value("${kalaha.pit.size-per-side}")
    private int pitSize;

    @Value("${kalaha.stone.size-per-pit}")
    private int stoneSize;

    private final KalahaEngine kalahaEngine;
    private final GameRepository<String, Game> gameRepository;

    public GameRedisService(KalahaEngine kalahaEngine,
                            GameRepository<String, Game> gameRepository) {
        this.kalahaEngine = kalahaEngine;
        this.gameRepository = gameRepository;
    }

    @Transactional
    @Override
    public String newGame() {
        Player southPlayer = new Player();
        southPlayer.setSide(Board.Side.SOUTH);
        southPlayer.setPosition(0);
        Player northPlayer = new Player();
        northPlayer.setSide(Board.Side.NORTH);
        northPlayer.setPosition(1);

        Game game = kalahaEngine.newGame(pitSize, stoneSize, List.of(southPlayer, northPlayer));
        Game savedGame = gameRepository.save(game);

        return savedGame.getId();
    }

    @Transactional
    @Override
    public Game move(String gameId, int pitId) {
        return gameRepository
                .findAndUpdate(gameId, game -> kalahaEngine.move(game, pitId));
    }

    @Transactional(readOnly = true)
    @Override
    public Game findById(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game NOT found"));
    }
}
