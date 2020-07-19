package com.kalaha.api.repository;

import com.kalaha.api.exception.ConcurrentOperationException;
import com.kalaha.api.exception.NotFoundException;
import com.kalaha.api.model.Game;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Repository
public class GameRedisRepository implements GameRepository<String, Game> {

    @Value("${redis.game.key}")
    private String redisHashKey;

    private final RedisTemplate<String, Game> template;

    public GameRedisRepository(RedisTemplate<String, Game> template) {
        this.template = template;
    }

    @Override
    public Game save(Game game) {
        String key = UUID.randomUUID().toString();
        game.setId(key);
        template.opsForHash().put(redisHashKey, key, game);
        return game;
    }

    @Override
    public Optional<Game> findById(String key) {
        Object value = template.opsForHash().get(redisHashKey, key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of((Game) value);
    }

    @Override
    public Game findAndUpdate(String key, Function<Game, Game> updateFn) {
        try {
            return template.execute(findAndUpdateCallback(key, updateFn));
        } catch (DataAccessException e) {
            throw new ConcurrentOperationException("Can't update game state");
        }
    }

    private SessionCallback<Game> findAndUpdateCallback(String key, Function<Game, Game> updateFn) {
        return new SessionCallback<>() {
            @SuppressWarnings("unchecked")
            @Override
            public <K, V> Game execute(@NonNull RedisOperations<K, V> operations)
                    throws DataAccessException {
                // Optimistic locking using check-and-set (CAS)
                // If there are race conditions and another client modifies the result for the same key
                // in the time between WATCH and EXEC call, the transaction will fail
                operations.watch((K) key);
                Game game = executeFind(key, operations);
                operations.multi();
                game = executeUpdate(game, updateFn, operations);
                operations.exec();
                return game;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <K, V> Game executeFind(String key, RedisOperations<K, V> operations) {
        Game game = (Game) operations.opsForHash().get((K) redisHashKey, key);
        if (game == null) {
            throw new NotFoundException("Game state not found");
        }
        return game;
    }

    @SuppressWarnings("unchecked")
    private <K, V> Game executeUpdate(Game game, Function<Game, Game> updateFn, RedisOperations<K, V> operations) {
        game = updateFn.apply(game);
        operations.opsForHash().put((K) redisHashKey, game.getId(), game);
        return game;
    }
}
