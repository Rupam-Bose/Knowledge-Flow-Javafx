package com.example.knowlwdgeflow.service;

import java.util.prefs.Preferences;

/**
 * Stores the last authenticated user ID using Java Preferences so
 * the app can auto-login on next launch. Only the user id is persisted.
 */
public class SessionService {
    private static final String PREF_NODE = "com.example.knowlwdgeflow.session";
    private static final String KEY_USER_ID = "userId";

    private final Preferences prefs = Preferences.userRoot().node(PREF_NODE);

    public void saveUserId(int userId) {
        prefs.putInt(KEY_USER_ID, userId);
    }

    public Integer getSavedUserId() {
        int id = prefs.getInt(KEY_USER_ID, -1);
        return id > 0 ? id : null;
    }

    public void clear() {
        prefs.remove(KEY_USER_ID);
    }
}

