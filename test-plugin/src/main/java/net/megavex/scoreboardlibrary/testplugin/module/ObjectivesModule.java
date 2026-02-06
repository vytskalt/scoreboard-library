package net.megavex.scoreboardlibrary.testplugin.module;

import net.megavex.scoreboardlibrary.api.objective.*;
import net.megavex.scoreboardlibrary.testplugin.ScoreboardPlugin;
import net.megavex.scoreboardlibrary.testplugin.TestTranslator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class ObjectivesModule implements Module, Listener {
  private final ScoreboardPlugin plugin;
  private ObjectiveManager manager;
  private ScoreboardObjective objective;

  public ObjectivesModule(final ScoreboardPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onEnable() {
    this.manager = plugin.scoreboardLibrary().createObjectiveManager();

    this.objective = this.manager.create("test");
    this.objective.value(text("List Objective"));
    this.objective.renderType(ObjectiveRenderType.INTEGER);
    this.objective.defaultScoreFormat(ScoreFormat.fixed(text("obj ").append(translatable(TestTranslator.KEY))));
    this.manager.display(ObjectiveDisplaySlot.playerList(), this.objective);

    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void onDisable() {
    HandlerList.unregisterAll(this);
    this.manager.close();
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    this.manager.addPlayer(player);
    this.objective.score(player.getName(), 5);
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    this.manager.removePlayer(player);
    this.objective.removeScore(player.getName());
  }
}
