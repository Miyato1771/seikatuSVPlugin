package org.mt17.seikatuSVPlugin.dailyQuest;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class dailyQuest implements Listener, CommandExecutor {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Economy economy;
    private final Map<UUID, QuestProgress> playerProgress = new HashMap<>();

    public dailyQuest(JavaPlugin plugin, FileConfiguration config, Economy economy) {
        this.plugin = plugin;
        this.config = config;
        this.economy = economy;
    }

    public void resetProgress() {
        playerProgress.clear();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

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
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

        progress.craftItem(event.getRecipe().getResult().getType(), 1);

        checkAndReward(player, progress, "craft");
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

        progress.enchantItem(1);

        checkAndReward(player, progress, "enchant");
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        QuestProgress progress = playerProgress.computeIfAbsent(playerUUID, k -> new QuestProgress());

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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0 && args[0].equalsIgnoreCase("progress")) {
                showProgress(player);
                return true;
            }
        }
        return false;
    }
}