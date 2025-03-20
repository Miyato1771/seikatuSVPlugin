package org.mt17.seikatuSVPlugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mt17.seikatuSVPlugin.dailyQuest.dailyQuest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public final class SeikatuSVPlugin extends JavaPlugin implements Listener {

    private static Economy economy = null;
    private FileConfiguration config;
    private dailyQuest dailyQuestInstance;
    private final loginBonus loginBonus = new loginBonus();

    @Override
    public void onEnable() {
        // config.ymlの読み込み
        saveDefaultConfig();
        config = getConfig();

        // Vaultがサーバーにあるか確認
        if (!setupEconomy()) {
            getLogger().severe("Vaultがありません! 経済系のものは有効化されません");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // イベント登録
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new dirtShovelShop(), this);
        getCommand("seikatuSV").setExecutor(new seikatuSVCommand(this));

        // dailyQuestのインスタンスを生成
        dailyQuestInstance = new dailyQuest(this, config, economy);
        getCommand("dailyquest").setExecutor(dailyQuestInstance);
        getServer().getPluginManager().registerEvents(dailyQuestInstance, this);
        checkAndResetDailyQuest();

        // PlaceholderAPIがサーバーにあるか確認
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getPluginManager().registerEvents(new InfoScoreBoard(), this);
        } else {
            getLogger().warning("PlaceholderAPIがありません! プレスホルダー系のものは有効化されません");
        }

        // GSitがサーバーにあるか確認
        if (Bukkit.getPluginManager().isPluginEnabled("GSit")) {
            Bukkit.getPluginManager().registerEvents(new gsitFlyCansel(), this);
        } else {
            getLogger().warning("GSitがありません! GSit系のものは有効化されません");
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;

        // デイリークエストの進捗をリセットする
    }
    private void checkAndResetDailyQuest() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String lastResetDate = config.getString("lastResetDate");

        if (lastResetDate == null || !lastResetDate.equals(todayDate)) {
            dailyQuestInstance.resetProgress();
            config.set("lastResetDate", todayDate);
            saveConfig();
            getLogger().info("Daily quest progress has been reset.");
        } else {
            for (String playerUUID : config.getConfigurationSection("players").getKeys(false)) {
                dailyQuestInstance.loadProgress(UUID.fromString(playerUUID));
            }
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    // プレイヤーがログインしたときに毎日クエストを割り当てる
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String lastLoginDate = config.getString("players." + playerUUID + ".lastLoginDate");

        if (lastLoginDate == null || !lastLoginDate.equals(todayDate)) {
            dailyQuestInstance.assignDailyQuests(playerUUID);
            config.set("players." + playerUUID + ".lastLoginDate", todayDate);
            saveConfig();
        }
        // ログイン時にデイリークエストを表示
        dailyQuestInstance.showAssignedQuests(player);
    }

    // ログインボーナス
    @EventHandler
    public void loginBonusEV(PlayerJoinEvent event) {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            loginBonus.loginBonus(event, config, economy);
            saveConfig();
        }
    }


}