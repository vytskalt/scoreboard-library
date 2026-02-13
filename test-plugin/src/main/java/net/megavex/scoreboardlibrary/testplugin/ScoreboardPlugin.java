package net.megavex.scoreboardlibrary.testplugin;

import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.testplugin.module.Module;
import net.megavex.scoreboardlibrary.testplugin.module.ObjectivesModule;
import net.megavex.scoreboardlibrary.testplugin.module.SidebarModule;
import net.megavex.scoreboardlibrary.testplugin.module.TeamsModule;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class ScoreboardPlugin extends JavaPlugin implements Listener {
  private final TestTranslator translator = new TestTranslator();
  private ScoreboardLibrary lib;
  private List<Module> modules;

  @Override
  public void onEnable() {
    this.getServer().getPluginManager().registerEvents(this, this);
    GlobalTranslator.get().addSource(this.translator);

    try {
      lib = ScoreboardLibrary.loadScoreboardLibrary(this);
    } catch (NoPacketAdapterAvailableException e) {
      this.getLogger().warning("No packet adapter found, disabling plugin");
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    this.modules = Arrays.asList(
      new SidebarModule(this),
      new TeamsModule(this),
      new ObjectivesModule(this)
    );
    for (final Module module : this.modules) {
      this.getLogger().info("Enabling " + module.getClass().getSimpleName());
      module.onEnable();
    }

    for (final World world : this.getServer().getWorlds()) {
      world.setAutoSave(false);
    }

    this.getServer().getScheduler().runTask(this, () -> {
      this.getLogger().info("");
      this.getLogger().info("### Server port: " + this.getServer().getPort() + " ###");
      this.getLogger().info("");
    });
  }

  @Override
  public void onDisable() {
    if (this.modules != null) {
      for (final Module module : this.modules) {
        module.onDisable();
      }
    }
    if (this.lib != null) {
      this.lib.close();
    }
    GlobalTranslator.get().removeSource(this.translator);
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    player.setOp(true);
    player.setGameMode(GameMode.CREATIVE);
    player.sendMessage("Server software: " + this.getServer().getName());
    player.sendMessage("Server version: " + this.getServer().getVersion());
    player.sendMessage("Is native adventure: " + ModernComponentProvider.IS_NATIVE_ADVENTURE);
  }

  @EventHandler
  public void onWeather(final WeatherChangeEvent event) {
    event.setCancelled(true);
  }

  public ScoreboardLibrary scoreboardLibrary() {
    return this.lib;
  }
}
