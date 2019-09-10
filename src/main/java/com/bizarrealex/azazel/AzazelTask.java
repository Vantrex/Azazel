package com.bizarrealex.azazel;

import com.bizarrealex.azazel.tab.TabTemplate;
import com.bizarrealex.azazel.tab.TabTitleAdapter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import com.bizarrealex.azazel.tab.Tab;
import com.bizarrealex.azazel.tab.TabAdapter;

import java.lang.reflect.Field;
import java.util.*;

/*
    TODO: Clean this thing up
 */
public class AzazelTask extends BukkitRunnable {

    private final Azazel azazel;

    private int headerFooterDelay;

    private int a;

    public AzazelTask(Azazel azazel, JavaPlugin plugin) {
        this.azazel = azazel;

        if(azazel.getHeaderFooterUpdateDelay() == -1) headerFooterDelay = -1;
        else headerFooterDelay = azazel.getHeaderFooterUpdateDelay();

        a = headerFooterDelay;

        if(headerFooterDelay == 0){
            headerFooterDelay = -1;
        }

        runTaskTimerAsynchronously(plugin, azazel.getUpdateDelay(),azazel.getUpdateDelay());
    }

    @Override
    public void run() {
        TabAdapter adapter = azazel.getAdapter();
        if (adapter != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Tab tab = azazel.getTabByPlayer(player);
                if (tab != null) {
                    TabTemplate template = adapter.getTemplate(player);

                    if (template == null || (template.getLeft().isEmpty() && template.getMiddle().isEmpty() && template.getRight().isEmpty())) {
                        for (Tab.TabEntryPosition position : tab.getPositions()) {
                            Team team = player.getScoreboard().getTeam(position.getKey());
                            if (team != null) {
                                if (team.getPrefix() != null && !team.getPrefix().isEmpty()) {
                                    team.setPrefix("");
                                }
                                if (team.getSuffix() != null && !team.getSuffix().isEmpty()) {
                                    team.setSuffix("");
                                }
                            }
                        }
                        continue;
                    }

                    for (int i = 0; i < 20 - template.getLeft().size(); i++) {
                        template.left("");
                    }

                    for (int i = 0; i < 20 - template.getMiddle().size(); i++) {
                        template.middle("");
                    }

                    for (int i = 0; i < 20 - template.getRight().size(); i++) {
                        template.right("");
                    }

                    List<List<String>> rows = Arrays.asList(template.getLeft(), template.getMiddle(), template.getRight(), template.getFarRight());
                    for (int l = 0; l < rows.size(); l++) {
                        for (int i = 0; i < rows.get(l).size(); i++) {
                            Team team = tab.getByLocation(l, i);
                            if (team != null) {
                                Map.Entry<String, String> prefixAndSuffix = getPrefixAndSuffix(rows.get(l).get(i));
                                String prefix = prefixAndSuffix.getKey();
                                String suffix = prefixAndSuffix.getValue();

                                if (team.getPrefix().equals(prefix) && team.getSuffix().equals(suffix)) {
                                    continue;
                                }

                                team.setPrefix(prefix);
                                team.setSuffix(suffix);
                            }
                        }
                    }
                }
            }
        }

        if(headerFooterDelay > -1){
            a++;
            if(a != headerFooterDelay) return;
        }
        TabTitleAdapter titleAdapter = azazel.getTabTitleAdapter();
        if(titleAdapter != null){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(azazel.getTablistTitle_1_8().contains(player.getUniqueId())){
                    String header = titleAdapter.getHeader(player);
                    String footer = titleAdapter.getFooter(player);
                    sendHeaderFooter(player,header,footer);
                }
            }
        }
    }

    private void sendHeaderFooter(Player player, String header, String footer) {
        IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try
        {
            Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, tabHeader);
            headerField.setAccessible(false);
            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, tabFooter);
            footerField.setAccessible(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }


    private Map.Entry<String, String> getPrefixAndSuffix(String text) {
        String prefix, suffix;

        text = ChatColor.translateAlternateColorCodes('&', text);

        if (text.length() > 16){
            int splitAt = text.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
            prefix = text.substring(0, splitAt);
            String suffixTemp = ChatColor.getLastColors(prefix) + text.substring(splitAt);
            suffix = (suffixTemp.substring(0, Math.min(suffixTemp.length(), 16)));
        } else {
            prefix = text;
            suffix = "";
        }

        return new AbstractMap.SimpleEntry<>(prefix, suffix);
    }
}
