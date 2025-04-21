package edu.duke.ece651.factorysim;

import java.util.*;
import java.util.function.Supplier;

public class ObjectPool<T extends PooledObject> {
    private final Queue<T> pool = new LinkedList<>();
    private final Supplier<T> createFunc;

    public ObjectPool(Supplier<T> createFunc) {
        this.createFunc = createFunc;
    }

    public ObjectPool(Supplier<T> createFunc, int size) {
        this(createFunc);
        for (int i = 0; i < size; i++) {
            pool.offer(createFunc.get());
        }
    }

    public T borrow() {
        T obj = pool.poll();
        if (obj == null) {
            obj = createFunc.get();
        }
        obj.onBorrowed();
        return obj;
    }

    public void release(T obj) {
        pool.offer(obj);
        obj.onReleased();
    }

    public int size() {
        return pool.size();
    }
}

