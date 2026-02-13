package net.megavex.scoreboardlibrary.testplugin.module;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.testplugin.ScoreboardPlugin;
import net.megavex.scoreboardlibrary.testplugin.TestTranslator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static net.kyori.adventure.text.format.TextDecoration.State.TRUE;

public final class TeamsModule implements Module, Listener {
  private final static List<NamedTextColor> NAMED_COLORS = new ArrayList<>(NamedTextColor.NAMES.values());

  private final Random random = new Random();
  private final ScoreboardPlugin plugin;
  private TeamManager manager;
  private ScoreboardTeam team;
  private BukkitTask task;

  public TeamsModule(final ScoreboardPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onEnable() {
    this.manager = plugin.scoreboardLibrary().createTeamManager();

    this.team = this.manager.createIfAbsent("epic");
    final TeamDisplay defaultDisplay = this.team.defaultDisplay();
    defaultDisplay.displayName(text("Epic Team"));
    defaultDisplay.prefix(text("[Epic Prefix] ").color(AQUA));
    defaultDisplay.suffix(space().append(Component.translatable(TestTranslator.KEY)));
    defaultDisplay.playerColor(RED);

    this.task = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
      final State state = defaultDisplay.prefix().decoration(BOLD);
      final Component newPrefix = defaultDisplay.prefix().decoration(BOLD, state == TRUE ? FALSE : TRUE);
      defaultDisplay.prefix(newPrefix);
      defaultDisplay.suffix(defaultDisplay.suffix().color(this.randomNamedColor()));
    }, 20, 20);

    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void onDisable() {
    this.task.cancel();
    HandlerList.unregisterAll(this);
    this.manager.close();
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    this.team.defaultDisplay().addEntry(player.getName());
    this.manager.addPlayer(player);
    this.team.defaultDisplay().removeEntry(player.getName());
    this.team.defaultDisplay().addEntry(player.getName());
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    this.manager.removePlayer(player);
    this.team.defaultDisplay().removeEntry(player.getName());
  }

  private NamedTextColor randomNamedColor() {
    return NAMED_COLORS.get(random.nextInt(NAMED_COLORS.size()));
  }
}
