package org.mt17.seikatuSVPlugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class loginBonus implements Listener {

    public void loginBonus(PlayerJoinEvent event, FileConfiguration config, Economy economy) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 今日の日付を取得
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // プレイヤーの前回ログイン日を取得
        String lastLoginDate = config.getString("players." + playerUUID + ".lastLogin");

        if (lastLoginDate == null || !lastLoginDate.equals(todayDate)) {
            // 初回ログインならボーナスを付与
            giveLoginBonus(player, economy);

            // ログイン日を更新
            config.set("players." + playerUUID + ".lastLogin", todayDate);
            player.sendMessage("ログインボーナスを受け取りました！ 100 コイン獲得！");
        }
    }
    private void giveLoginBonus(Player player, Economy economy) {
        if (economy != null) {
            economy.depositPlayer(player, 100);
        }
    }
}
