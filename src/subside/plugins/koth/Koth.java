package subside.plugins.koth;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import subside.plugins.koth.area.Area;
import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.area.SingleLootChest;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.scoreboard.SBManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Koth extends JavaPlugin {
	private @Getter static Koth plugin;
	private @Getter static WorldEditPlugin worldEdit;

	@Override
	public void onEnable() {
		plugin = this;

		worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		getCommand("koth").setExecutor(new CommandHandler());
		init();
		
	}

    @SuppressWarnings("deprecation")
	public void init(){
        KothHandler.stopAllKoths();
        this.saveDefaultConfig();
        this.reloadConfig();
	    new ConfigHandler(this.getConfig());
        Lang.load(this);
        KothLoader.load();
        ScheduleHandler.load();
        
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(SBManager.getManager(), this);
        
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                KothHandler.update();
            }
        }, 20, 20);

        if (ConfigHandler.getCfgHandler().isUsePlayerMoveEvent()) {
            getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        }
	}

	@Override
	public void onDisable() {
		SBManager.getManager().clearAll();
		
		for(Area area : KothHandler.getAvailableAreas()){
			for(Player player : Bukkit.getOnlinePlayers()){
				if(area.getInventory().getViewers().contains(player)){
					player.closeInventory();
				}
			}
		}
		if(ConfigHandler.getCfgHandler().isSingleLootChest()){
			for(Player player : Bukkit.getOnlinePlayers()){
				if(SingleLootChest.getInventory().getViewers().contains(player)){
					player.closeInventory();
				}
			}
		}
	}
}
