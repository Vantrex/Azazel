package com.bizarrealex.azazel.tab.example;

import com.bizarrealex.azazel.tab.TabTitleAdapter;
import org.bukkit.entity.Player;

public class ExampleTabTitleAdapter implements TabTitleAdapter {
    @Override
    public String getHeader(Player player) {
        return "§cHeader" + "\n" + "§7Gamemode: §d" + player.getGameMode().name();
    }

    @Override
    public String getFooter(Player player) {
        return "§cFooter" + "\n" + "§7Ping: §d" + player.spigot().getPing();
    }
}
