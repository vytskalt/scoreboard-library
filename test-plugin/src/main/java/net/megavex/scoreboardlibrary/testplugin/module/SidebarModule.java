package net.megavex.scoreboardlibrary.testplugin.module;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.LineDrawable;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.implementation.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.SidebarTask;
import net.megavex.scoreboardlibrary.testplugin.ScoreboardPlugin;
import net.megavex.scoreboardlibrary.testplugin.TestTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public final class SidebarModule implements Module, Listener, SidebarComponent {
  private final ScoreboardPlugin plugin;
  private Sidebar sidebar;
  private int tickCounter;
  private BukkitTask timer;

  public SidebarModule(final ScoreboardPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onEnable() {
    this.sidebar = plugin.scoreboardLibrary().createSidebar();

    final SidebarComponent title = SidebarComponent.dynamicLine(() -> text("Sidebar Test", hueColor()));

    final ComponentSidebarLayout layout = new ComponentSidebarLayout(title, this);
    this.timer = this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
      this.tickCounter++;
      layout.apply(sidebar);

      if ((this.tickCounter / 20) % 10 == 0) {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
          this.sidebar.removePlayer(player);
        }
      } else {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
          this.sidebar.addPlayer(player);
        }
      }
    }, 1, 1);

    this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void onDisable() {
    HandlerList.unregisterAll(this);
    this.timer.cancel();
    this.sidebar.close();
    this.sidebar = null;
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    sidebar.removePlayer(event.getPlayer());
  }

  @EventHandler
  public void onSneak(PlayerToggleSneakEvent event) {
    if (!event.isSneaking()) return;
    for (final Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage("Reloading you");
      ((PlayerDependantLocaleSidebar) this.sidebar).taskQueue().add(new SidebarTask.ReloadPlayer(player));
    }
  }

  @Override
  public void draw(final @NotNull LineDrawable drawable) {
    drawable.drawLine(empty(), ScoreFormat.styled(style(this.hueColor())));
    drawable.drawLine(text("Static line"));
    drawable.drawLine(text("Tick counter: ").append(text(this.tickCounter, color(0x00FF00))));

    if ((this.tickCounter / 20) % 2 == 0) {
      drawable.drawLine(text("Disappearing line"), ScoreFormat.styled(style(DARK_RED)));
    }

    drawable.drawLine(text("Your locale x2: ").append(Component.translatable(TestTranslator.KEY)), ScoreFormat.fixed(translatable(TestTranslator.KEY)));

    drawable.drawLine(text("VeryLongLineThatShouldBeCutOffFor1.12.2AndBelowPlayersButNotOnNewerVersions", AQUA, BOLD));
    drawable.drawLine(empty());

    drawable.drawLine(text("github.com/megavexnetwork/scoreboard-library", YELLOW));
  }

  private TextColor hueColor() {
    final float hue = ((this.tickCounter * 5) % 360) / 360f;
    final Color awtColor = Color.getHSBColor(hue, 0.8f, 0.9f);
    return color(awtColor.getRGB());
  }
}
