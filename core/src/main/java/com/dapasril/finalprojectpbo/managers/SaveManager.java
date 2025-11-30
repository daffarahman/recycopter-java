package com.dapasril.finalprojectpbo.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.Comparator;

public class SaveManager {
    private static SaveManager instance;
    private static final String SAVE_FILE = "saves.json";

    private Array<PlayerData> leaderboard;
    private Json json;

    private SaveManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        leaderboard = new Array<>();
        loadSaves();
    }

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    public void loadSaves() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (file.exists()) {
            try {
                @SuppressWarnings("unchecked")
                Array<PlayerData> loadedData = json.fromJson(Array.class, PlayerData.class, file);
                if (loadedData != null) {
                    leaderboard = loadedData;
                }
            } catch (Exception e) {
                Gdx.app.error("SaveManager", "Error loading saves", e);
            }
        }
        sortLeaderboard();
    }

    public void saveGame(String username, int level, int collected, int recycled) {
        PlayerData existingPlayer = null;
        for (PlayerData data : leaderboard) {
            if (data.username.equals(username)) {
                existingPlayer = data;
                break;
            }
        }

        if (existingPlayer != null) {
            // Update existing player
            if (level > existingPlayer.highestLevel) {
                existingPlayer.highestLevel = level;
            }
            existingPlayer.totalCollected += collected;
            existingPlayer.totalRecycled += recycled;
        } else {
            // Create new player
            PlayerData newPlayer = new PlayerData();
            newPlayer.username = username;
            newPlayer.highestLevel = level;
            newPlayer.totalCollected = collected;
            newPlayer.totalRecycled = recycled;
            leaderboard.add(newPlayer);
        }

        sortLeaderboard();
        saveToDisk();
    }

    private void sortLeaderboard() {
        // Sort by highest level descending, then total collected descending
        leaderboard.sort(new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData o1, PlayerData o2) {
                if (o1.highestLevel != o2.highestLevel) {
                    return Integer.compare(o2.highestLevel, o1.highestLevel);
                }
                return Integer.compare(o2.totalCollected, o1.totalCollected);
            }
        });
    }

    private void saveToDisk() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        try {
            file.writeString(json.prettyPrint(leaderboard), false);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error saving game", e);
        }
    }

    public Array<PlayerData> getLeaderboard() {
        return leaderboard;
    }

    public static class PlayerData {
        public String username;
        public int highestLevel;
        public int totalCollected;
        public int totalRecycled;

        public PlayerData() {
        }
    }
}
