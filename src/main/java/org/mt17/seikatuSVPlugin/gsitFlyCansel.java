package org.mt17.seikatuSVPlugin;

import dev.geco.gsit.api.event.PlayerPlayerSitEvent;
import dev.geco.gsit.api.event.PlayerStopPlayerSitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashSet;
import java.util.Set;

public class gsitFlyCansel implements Listener {

    private final Set<Player> sittingPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Bukkit.getServer().broadcastMessage("フライト検知");
        Player player = event.getPlayer();
        if (event.isFlying() && sittingPlayers.contains(player)) {
            player.setFlying(false);
            sittingPlayers.remove(player);
        }
    }

    @EventHandler
    public void onPlayerSit(PlayerPlayerSitEvent event) {
        Bukkit.getServer().broadcastMessage("座り検知");
        sittingPlayers.add(event.getPlayer());
    }

    @EventHandler
    public void onPlayerStopSit(PlayerStopPlayerSitEvent event) {
        Bukkit.getServer().broadcastMessage("座り終了検知");
        sittingPlayers.remove(event.getPlayer());
    }
}