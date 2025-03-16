package org.mt17.seikatuSVPlugin.dailyQuest;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class dailyQuest implements Listener, CommandExecutor {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Economy economy;
    private final Map<UUID, QuestProgress> playerProgress = new HashMap<>();
    private final List<String> questList = Arrays.asList("diamond", "iron", "gold", "redstone", "lapis", "copper", "coal", "quartz", "craft", "enchant", "cod", "salmon", "pufferfish", "tropical_fish", "move");

    public dailyQuest(JavaPlugin plugin, FileConfiguration config, Economy economy) {
        this.plugin = plugin;
        this.config = config;
        this.economy = economy;
    }

    public void resetProgress() {
        playerProgress.clear();
    }

    public void assignDailyQuests(UUID playerUUID) {
        Collections.shuffle(questList);
        List<String> assignedQuests = questList.subList(0, 3);
        config.set("players." + playerUUID + ".assignedQuests", assignedQuests);
        plugin.saveConfig();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

        if (!isQuestAssigned(playerUUID, "diamond") && material == Material.DIAMOND_ORE) return;
        if (!isQuestAssigned(playerUUID, "iron") && material == Material.IRON_ORE) return;
        if (!isQuestAssigned(playerUUID, "gold") && material == Material.GOLD_ORE) return;
        if (!isQuestAssigned(playerUUID, "redstone") && material == Material.REDSTONE_ORE) return;
        if (!isQuestAssigned(playerUUID, "lapis") && material == Material.LAPIS_ORE) return;
        if (!isQuestAssigned(playerUUID, "copper") && material == Material.COPPER_ORE) return;
        if (!isQuestAssigned(playerUUID, "coal") && material == Material.COAL_ORE) return;
        if (!isQuestAssigned(playerUUID, "quartz") && material == Material.NETHER_QUARTZ_ORE) return;

        switch (material) {
            case DIAMOND_ORE:
                progress.collectItem("diamond", 1);
                checkAndReward(player, progress, "diamond");
                break;
            case IRON_ORE:
                progress.collectItem("iron", 1);
                checkAndReward(player, progress, "iron");
                break;
            case GOLD_ORE:
                progress.collectItem("gold", 1);
                checkAndReward(player, progress, "gold");
                break;
            case REDSTONE_ORE:
                progress.collectItem("redstone", 1);
                checkAndReward(player, progress, "redstone");
                break;
            case LAPIS_ORE:
                progress.collectItem("lapis", 1);
                checkAndReward(player, progress, "lapis");
                break;
            case COPPER_ORE:
                progress.collectItem("copper", 1);
                checkAndReward(player, progress, "copper");
                break;
            case COAL_ORE:
                progress.collectItem("coal", 1);
                checkAndReward(player, progress, "coal");
                break;
            case NETHER_QUARTZ_ORE:
                progress.collectItem("quartz", 1);
                checkAndReward(player, progress, "quartz");
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        savePlayerProgress(playerUUID, playerProgress.get(playerUUID));
        plugin.saveConfig();
    }

    public void savePlayerProgress(UUID playerUUID, QuestProgress progress) {
        if (progress != null) {
            config.set("players." + playerUUID + ".progress", progress.getProgressMap());
            config.set("players." + playerUUID + ".completed", progress.getCompletedMap());
            config.set("players." + playerUUID + ".disabled", progress.getDisabledMap());
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

        if (!isQuestAssigned(playerUUID, "craft")) return;

        progress.craftItem(event.getRecipe().getResult().getType(), 1);

        checkAndReward(player, progress, "craft");
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

        if (!isQuestAssigned(playerUUID, "enchant")) return;

        progress.enchantItem(1);

        checkAndReward(player, progress, "enchant");
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

        if (!isQuestAssigned(playerUUID, "fish")) return;

        switch (event.getCaught().getType()) {
            case COD:
                progress.fishItem("cod", 1);
                checkAndReward(player, progress, "cod");
                break;
            case SALMON:
                progress.fishItem("salmon", 1);
                checkAndReward(player, progress, "salmon");
                break;
            case PUFFERFISH:
                progress.fishItem("pufferfish", 1);
                checkAndReward(player, progress, "pufferfish");
                break;
            case TROPICAL_FISH:
                progress.fishItem("tropical_fish", 1);
                checkAndReward(player, progress, "tropical_fish");
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

        if (!isQuestAssigned(playerUUID, "move")) return;

        progress.move(1);

        checkAndReward(player, progress, "move");
    }

    private void checkAndReward(Player player, QuestProgress progress, String questPart) {
        if (progress.isCompleted(questPart) && !progress.isDisabled(questPart)) {
            economy.depositPlayer(player, 100); // 報酬を与える
            player.sendMessage("デイリークエストの " + questPart + " を完了しました！ 100 コイン獲得！");
            progress.disableQuest(questPart); // クエストを無効化
        }
    }

    public void showProgress(Player player) {
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.get(playerUUID);
        if (progress == null) {
            player.sendMessage("デイリークエストの進捗はありません。");
            return;
        }

        player.sendMessage("デイリークエストの進捗:");
        player.sendMessage("ダイヤモンド: " + progress.getProgress("diamond") + "/1");
        player.sendMessage("鉄: " + progress.getProgress("iron") + "/32");
        player.sendMessage("金: " + progress.getProgress("gold") + "/32");
        player.sendMessage("レッドストーン: " + progress.getProgress("redstone") + "/64");
        player.sendMessage("ラピスラズリ: " + progress.getProgress("lapis") + "/64");
        player.sendMessage("銅: " + progress.getProgress("copper") + "/64");
        player.sendMessage("石炭: " + progress.getProgress("coal") + "/64");
        player.sendMessage("クオーツ: " + progress.getProgress("quartz") + "/64");
        player.sendMessage("クラフト: " + progress.getProgress("craft") + "/100");
        player.sendMessage("エンチャント: " + progress.getProgress("enchant") + "/3");
        player.sendMessage("生鱈: " + progress.getProgress("cod") + "/16");
        player.sendMessage("生鮭: " + progress.getProgress("salmon") + "/16");
        player.sendMessage("フグ: " + progress.getProgress("pufferfish") + "/1");
        player.sendMessage("熱帯魚: " + progress.getProgress("tropical_fish") + "/1");
        player.sendMessage("移動距離: " + progress.getProgress("move") + "/1000");
    }

    public void saveProgress() {
        for (Map.Entry<UUID, QuestProgress> entry : playerProgress.entrySet()) {
            UUID playerUUID = entry.getKey();
            QuestProgress progress = entry.getValue();
            config.set("players." + playerUUID + ".progress", progress.getProgressMap());
            config.set("players." + playerUUID + ".completed", progress.getCompletedMap());
            config.set("players." + playerUUID + ".disabled", progress.getDisabledMap());
        }
        plugin.saveConfig();
    }



    public void loadProgress(UUID playerUUID) {
        if (config.contains("players." + playerUUID)) {
            ConfigurationSection progressSection = config.getConfigurationSection("players." + playerUUID + ".progress");
            ConfigurationSection completedSection = config.getConfigurationSection("players." + playerUUID + ".completed");
            ConfigurationSection disabledSection = config.getConfigurationSection("players." + playerUUID + ".disabled");

            Map<String, Integer> progressMap = new HashMap<>();
            Map<String, Boolean> completedMap = new HashMap<>();
            Map<String, Boolean> disabledMap = new HashMap<>();

            if (progressSection != null) {
                for (String key : progressSection.getKeys(false)) {
                    progressMap.put(key, progressSection.getInt(key));
                }
            }
            if (completedSection != null) {
                for (String key : completedSection.getKeys(false)) {
                    completedMap.put(key, completedSection.getBoolean(key));
                }
            }
            if (disabledSection != null) {
                for (String key : disabledSection.getKeys(false)) {
                    disabledMap.put(key, disabledSection.getBoolean(key));
                }
            }

            QuestProgress progress = new QuestProgress(progressMap, completedMap, disabledMap);
            playerProgress.put(playerUUID, progress);
        }
    }

    private boolean isQuestAssigned(UUID playerUUID, String questPart) {
        List<String> assignedQuests = config.getStringList("players." + playerUUID + ".assignedQuests");
        return assignedQuests.stream().anyMatch(quest -> quest.equalsIgnoreCase(questPart));
    }

    public void showAssignedQuests(Player player) {
        UUID playerUUID = player.getUniqueId();
        List<String> assignedQuests = config.getStringList("players." + playerUUID + ".assignedQuests");
        if (assignedQuests == null || assignedQuests.isEmpty()) {
            player.sendMessage("クエストがありません.");
        } else {
            player.sendMessage("クエスト一覧:");
            for (String quest : assignedQuests) {
                player.sendMessage("- " + quest);
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("progress")) {
                    showProgress(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("show")) {
                    showAssignedQuests(player);
                    return true;
                }
            } else {
                player.sendMessage("Please provide a valid argument.");
            }
        }
        return false;
    }
}