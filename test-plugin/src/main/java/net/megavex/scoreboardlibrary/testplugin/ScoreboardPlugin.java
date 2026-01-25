package net.megavex.scoreboardlibrary.testplugin;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class ScoreboardPlugin extends JavaPlugin {
  private ScoreboardLibrary lib;
  private List<Module> modules;

  @Override
  public void onEnable() {
    try {
      lib = ScoreboardLibrary.loadScoreboardLibrary(this);
    } catch (NoPacketAdapterAvailableException e) {
      this.getLogger().warning("No packet adapter found, disabling plugin");
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    this.modules = Collections.singletonList(new SidebarModule(this));
    for (final Module module : this.modules) {
      this.getLogger().info("Enabling " + module.getClass().getSimpleName());
      module.onEnable();
    }
  }

  @Override
  public void onDisable() {
    for (final Module module : this.modules) {
      module.onDisable();
    }
    this.modules = null;
    this.lib.close();
    this.lib = null;
  }

  public ScoreboardLibrary scoreboardLibrary() {
    return this.lib;
  }
}
