package com.bizarrealex.azazel.tab.example;

import com.bizarrealex.azazel.Azazel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Getter private Azazel azazel;

    @Override
    public void onEnable() {
        this.azazel = new Azazel(this, new ExampleTabAdapter(), new ExampleTabTitleAdapter());
    }

    private void addTabAfterJoin(Player player){
        this.azazel.addTab(player);
    }
}
