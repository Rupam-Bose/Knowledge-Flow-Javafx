package com.example.knowlwdgeflow.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Shared in-memory like state so Home and Full Blog stay in sync.
 */
public final class LikeState {
    private static final LikeState INSTANCE = new LikeState();
    private final Map<Integer, Boolean> liked = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> counts = new ConcurrentHashMap<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    private LikeState() {}

    public static LikeState getInstance() {
        return INSTANCE;
    }

    /** Ensure we have defaults for a blog id. */
    public void ensure(int blogId) {
        liked.putIfAbsent(blogId, false);
        counts.putIfAbsent(blogId, 0);
    }

    public boolean isLiked(int blogId) {
        return liked.getOrDefault(blogId, false);
    }

    public int getCount(int blogId) {
        return counts.getOrDefault(blogId, 0);
    }

    /** Toggle like state and adjust count. */
    public void toggle(int blogId) {
        ensure(blogId);
        boolean current = liked.get(blogId);
        int count = counts.get(blogId);
        if (current) {
            counts.put(blogId, Math.max(0, count - 1));
        } else {
            counts.put(blogId, count + 1);
        }
        liked.put(blogId, !current);
        notifyListeners();
    }

    public void addListener(Runnable listener) {
        if (listener != null) listeners.add(listener);
    }

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Runnable r : listeners) {
            try { r.run(); } catch (Exception ignored) {}
        }
    }
}
