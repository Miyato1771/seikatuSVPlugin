package org.mt17.seikatuSVPlugin.dailyQuest;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class QuestProgress {
    private final Map<String, Integer> progress = new HashMap<>();
    private final Map<String, Boolean> completed = new HashMap<>();
    private final Map<String, Boolean> disabled = new HashMap<>();


    // デフォルトコンストラクタ
    public QuestProgress() {
    }
    public QuestProgress(Map<String, Integer> progress, Map<String, Boolean> completed, Map<String, Boolean> disabled) {
        this.progress.putAll(progress);
        this.completed.putAll(completed);
        this.disabled.putAll(disabled);
    }

    public Map<String, Integer> getProgressMap() {
        return new HashMap<>(progress);
    }

    public Map<String, Boolean> getCompletedMap() {
        return new HashMap<>(completed);
    }

    public Map<String, Boolean> getDisabledMap() {
        return new HashMap<>(disabled);
    }

    public void collectItem(String item, int amount) {
        if (!isDisabled(item) && !isCompleted(item)) {
            progress.put(item, progress.getOrDefault(item, 0) + amount);
            checkCompletion(item);
        }
    }

    public void craftItem(Material item, int amount) {
        if (!isDisabled("craft") && !isCompleted("craft")) {
            progress.put("craft", progress.getOrDefault("craft", 0) + amount);
            checkCompletion("craft");
        }
    }

    public void enchantItem(int amount) {
        if (!isDisabled("enchant") && !isCompleted("enchant")) {
            progress.put("enchant", progress.getOrDefault("enchant", 0) + amount);
            checkCompletion("enchant");
        }
    }

    public void fishItem(String fish, int amount) {
        if (!isDisabled(fish) && !isCompleted(fish)) {
            progress.put(fish, progress.getOrDefault(fish, 0) + amount);
            checkCompletion(fish);
        }
    }

    public void move(int distance) {
        if (!isDisabled("move") && !isCompleted("move")) {
            progress.put("move", progress.getOrDefault("move", 0) + distance);
            checkCompletion("move");
        }
    }

    public int getProgress(String item) {
        return progress.getOrDefault(item, 0);
    }

    public boolean isCompleted(String item) {
        return completed.getOrDefault(item, false);
    }

    public boolean isDisabled(String item) {
        return disabled.getOrDefault(item, false);
    }

    private void checkCompletion(String item) {
        switch (item) {
            case "diamond":
                if (progress.get(item) >= 1) completed.put(item, true);
                break;
            case "iron":
                if (progress.get(item) >= 32) completed.put(item, true);
                break;
            case "gold":
                if (progress.get(item) >= 32) completed.put(item, true);
                break;
            case "redstone":
                if (progress.get(item) >= 64) completed.put(item, true);
                break;
            case "lapis":
                if (progress.get(item) >= 64) completed.put(item, true);
                break;
            case "copper":
                if (progress.get(item) >= 64) completed.put(item, true);
                break;
            case "coal":
                if (progress.get(item) >= 64) completed.put(item, true);
                break;
            case "quartz":
                if (progress.get(item) >= 64) completed.put(item, true);
                break;
            case "craft":
                if (progress.get(item) >= 100) completed.put(item, true);
                break;
            case "enchant":
                if (progress.get(item) >= 3) completed.put(item, true);
                break;
            case "cod":
                if (progress.get(item) >= 16) completed.put(item, true);
                break;
            case "salmon":
                if (progress.get(item) >= 16) completed.put(item, true);
                break;
            case "pufferfish":
                if (progress.get(item) >= 1) completed.put(item, true);
                break;
            case "tropical_fish":
                if (progress.get(item) >= 1) completed.put(item, true);
                break;
            case "move":
                if (progress.get(item) >= 1000) completed.put(item, true);
                break;
        }
    }

    public void disableQuest(String item) {
        disabled.put(item, true);
    }

    public boolean isComplete() {
        return completed.values().stream().anyMatch(Boolean::booleanValue);
    }
}