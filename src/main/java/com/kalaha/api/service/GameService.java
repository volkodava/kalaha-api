package com.kalaha.api.service;

import com.kalaha.api.model.Game;

public interface GameService {

    String newGame();

    Game move(String gameId, int pitId);

    Game findById(String gameId);
}
