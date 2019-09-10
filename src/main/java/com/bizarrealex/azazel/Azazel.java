package com.bizarrealex.azazel;

import com.bizarrealex.azazel.tab.ClientVersion;
import com.bizarrealex.azazel.tab.Tab;

import com.bizarrealex.azazel.tab.TabAdapter;
import com.bizarrealex.azazel.tab.TabTitleAdapter;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Azazel implements Listener {

    @Getter private static Azazel azazel;

    @Getter private final JavaPlugin plugin;
    @Getter private final Map<UUID, Tab> tabs;
    @Getter @Setter private TabAdapter adapter;
    @Getter @Setter private TabTitleAdapter tabTitleAdapter;
    @Getter private final List<UUID> tablistTitle_1_8;

    @Getter @Setter private boolean disableJoinListener;
    @Getter @Setter private long updateDelay = 15;
    @Getter @Setter private int headerFooterUpdateDelay = -1;

    private boolean taskStarted;

    public Azazel(JavaPlugin plugin) {
        azazel = this;
        this.plugin = plugin;
        this.tablistTitle_1_8 = new ArrayList<>();
        this.tabs = new ConcurrentHashMap<>();
        this.disableJoinListener = false;
        this.taskStarted = false;

        if (Bukkit.getMaxPlayers() < 60) {
            Bukkit.getLogger().severe("There aren't 60 player slots, this will fuck up the tab list."); //TODO: Possibly set max players to 60?
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ClientVersion.getVersion(player) == ClientVersion.v1_7) {
                if (!(tabs.containsKey(player.getUniqueId()))) {
                    tabs.put(player.getUniqueId(), new Tab(player, true, this));
                }
            }
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if(!taskStarted){
                startTask();
            }
        },20 * 10);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void startTask(){
        taskStarted = true;
        new AzazelTask(this, plugin);
    }

    public Azazel(JavaPlugin plugin, TabAdapter adapter) {
        this(plugin);

        this.adapter = adapter;
    }
    public Azazel(JavaPlugin plugin, TabAdapter adapter, TabTitleAdapter tabTitleAdapter) {
        this(plugin);
        this.tabTitleAdapter = tabTitleAdapter;
        this.adapter = adapter;
    }

    public Azazel(JavaPlugin plugin, TabTitleAdapter tabTitleAdapter) {
        this(plugin);
        this.tabTitleAdapter = tabTitleAdapter;
    }

    public Tab getTabByPlayer(Player player) {
        return tabs.get(player.getUniqueId());
    }



    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        if(disableJoinListener) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(ClientVersion.getVersion(player) == ClientVersion.v1_8) continue;
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,((CraftPlayer)event.getPlayer()).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        }
        if(ClientVersion.getVersion(event.getPlayer()) == ClientVersion.v1_8) return;
        new BukkitRunnable() {
            @Override
            public void run() {

                tabs.put(event.getPlayer().getUniqueId(), new Tab(event.getPlayer(), true, Azazel.this));
            }
        }.runTaskLater(plugin, 1L);
    }

    public void addTab(Player player){
        for (Player online : Bukkit.getOnlinePlayers()) {
            if(ClientVersion.getVersion(online) == ClientVersion.v1_8) continue;
            if(!tabs.containsKey(online.getUniqueId())) continue; //
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,((CraftPlayer)player.getPlayer()).getHandle());
            ((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);
        }
        if(ClientVersion.getVersion(player) == ClientVersion.v1_8) return;
        new BukkitRunnable() {
            @Override
            public void run() {

                tabs.put(player.getUniqueId(), new Tab(player, true, Azazel.this));
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        tabs.remove(event.getPlayer().getUniqueId());
        tablistTitle_1_8.remove(event.getPlayer().getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {

            if(tabs.containsKey(other.getUniqueId())) continue;

            if (ClientVersion.getVersion(event.getPlayer()) == ClientVersion.v1_8) {
                Tab tab = getTabByPlayer(event.getPlayer());

                if (tab != null && tab.getElevatedTeam() != null) {
                    tab.getElevatedTeam().removeEntry(event.getPlayer().getName());
                }

            }

        }
    }
}
