package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

// Implementation for versions 1.20.3+
// This is not yet being used as it might break on servers with plugins such as ViaBackwards,
// however it will once Mojang decides to remove legacy chat format support
public final class PostModernLocaleLine implements LocaleLine {
  private final GlobalLineInfo info;
  private final SidebarLineHandler handler;

  public PostModernLocaleLine(GlobalLineInfo info, SidebarLineHandler handler) {
    this.info = info;
    this.handler = handler;
  }

  @Override
  public @NotNull GlobalLineInfo info() {
    return info;
  }

  @Override
  public void value(@NotNull Component renderedComponent) {
  }

  @Override
  public void updateTeam() {
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players) {
    handler.localeLineHandler()
      .sidebar()
      .packetAdapter()
      .sendScore(players, info.player(), info.objectiveScore(), info.value(), info.scoreFormat());
  }

  @Override
  public void show(@NotNull Collection<Player> players) {
    sendScore(players);
  }

  @Override
  public void hide(@NotNull Collection<Player> players) {
    handler.localeLineHandler().sidebar().packetAdapter().removeScore(players, info.player());
  }
}
