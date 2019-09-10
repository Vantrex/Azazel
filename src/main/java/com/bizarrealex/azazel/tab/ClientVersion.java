package com.bizarrealex.azazel.tab;

import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;


public enum ClientVersion {

    v1_7, v1_8;

    public static ClientVersion getVersion(Player player) {

        int version = ProtocolSupportAPI.getProtocolVersion(player).getId();
        return  version > 5 ? v1_8 : v1_7;
    }
}
