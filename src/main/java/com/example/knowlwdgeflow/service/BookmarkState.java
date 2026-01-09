package com.example.knowlwdgeflow.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** In-memory shared bookmark state to sync between Home and Full Blog. */
public final class BookmarkState {
    private static final BookmarkState INSTANCE = new BookmarkState();
    private final Map<Integer, Boolean> bookmarked = new ConcurrentHashMap<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    private BookmarkState() {}

    public static BookmarkState getInstance() {
        return INSTANCE;
    }

    public void ensure(int blogId) {
        bookmarked.putIfAbsent(blogId, false);
    }

    public boolean isBookmarked(int blogId) {
        return bookmarked.getOrDefault(blogId, false);
    }

    public void toggle(int blogId) {
        ensure(blogId);
        bookmarked.put(blogId, !bookmarked.get(blogId));
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

