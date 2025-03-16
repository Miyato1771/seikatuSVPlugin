package org.mt17.seikatuSVPlugin;

import dev.geco.gsit.api.GSitAPI;
import dev.geco.gsit.api.event.PlayerPlayerSitEvent;
import dev.geco.gsit.api.event.PlayerStopPlayerSitEvent;
import dev.geco.gsit.object.GStopReason;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import java.util.HashMap;
import java.util.Map;


public class gsitFlyCansel implements Listener {

    HashMap<Player,Player> PlayerOntarget = new HashMap<>();
    @EventHandler
    public void onPlayerToggleFlight(EntityToggleGlideEvent event) {
        Player target;
        Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        if(PlayerOntarget.get(player) != null) {
            target = PlayerOntarget.get(player);
        }
        else {
            return;
        }
        GSitAPI.stopPlayerSit(target, GStopReason.TELEPORT);
        target.sendMessage("§c飛行するときは座れません！");
    }

    // ③ 飛行中に座るのを防ぐ（PreEntitySitEvent を使用）
    @EventHandler
    public void onPlayerTryToSit(PlayerPlayerSitEvent event) {
        Player target = event.getTarget();
        PlayerOntarget.put(target, event.getPlayer());
        // プレイヤーが飛行中またはエリトラ滑空中ならキャンセル
        if (target.isFlying() || target.isGliding()) {
            GSitAPI.stopPlayerSit(target, GStopReason.TELEPORT);
            event.getPlayer().sendMessage("§c飛行するときは座れません！");
            PlayerOntarget.remove(target);
        }
    }
    @EventHandler
    public void Leaveplyersit(PlayerStopPlayerSitEvent event) {
        if(getKey(PlayerOntarget,event.getPlayer()) != null) {
            PlayerOntarget.remove(getKey(PlayerOntarget,event.getPlayer()));
        }
    }
    public <K, V> K getKey(Map<K, V> map, V value)
    {
        return map.entrySet().stream()
                .filter(entry -> value.equals(entry.getValue()))
                .findFirst().map(Map.Entry::getKey)
                .orElse(null);
    }
}