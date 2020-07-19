package com.kalaha.api.service;

public interface GameService<K, V> {

    K newGame();

    V move(K gameId, int pitId);

    V findById(K gameId);
}
