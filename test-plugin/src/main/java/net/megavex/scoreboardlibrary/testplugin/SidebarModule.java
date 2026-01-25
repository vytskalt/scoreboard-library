package net.megavex.scoreboardlibrary.testplugin;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SidebarModule implements Module, Listener {
  private final ScoreboardPlugin plugin;
  private Sidebar sidebar;

  public SidebarModule(ScoreboardPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onEnable() {
    this.sidebar = plugin.scoreboardLibrary().createSidebar();
    this.sidebar.title(Component.text("Hello sidebar"));
    this.sidebar.line(0, Component.empty());
    this.sidebar.line(1, Component.text("line 1"));
    this.sidebar.line(2, Component.text("line 2"));
    this.sidebar.line(3, Component.text("line 3"));
    this.sidebar.line(4, Component.empty());
    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void onDisable() {
    HandlerList.unregisterAll(this);
    this.sidebar.close();
    this.sidebar = null;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    sidebar.addPlayer(event.getPlayer());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    sidebar.removePlayer(event.getPlayer());
  }
}
