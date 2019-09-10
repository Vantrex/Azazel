package com.bizarrealex.azazel.tab;

import org.bukkit.entity.Player;

public interface TabTitleAdapter {

    String getHeader(Player player);

    String getFooter(Player player);

}
