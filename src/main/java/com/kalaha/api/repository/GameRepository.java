package com.kalaha.api.repository;

import java.util.Optional;
import java.util.function.Function;

public interface GameRepository<K, V> {
    V save(V value);

    Optional<V> findById(K key);

    V findAndUpdate(K key, Function<V, V> updateFn);
}
