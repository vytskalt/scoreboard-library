package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static net.kyori.adventure.text.Component.empty;

// Implementation for versions above 1.13
public class ModernLocaleLine implements LocaleLine, ImmutableTeamProperties<Component> {
  private final GlobalLineInfo info;
  private final SidebarLineHandler handler;
  private final Collection<String> entries;
  private final TeamDisplayPacketAdapter packetAdapter;

  public ModernLocaleLine(GlobalLineInfo info, SidebarLineHandler handler) {
    this.info = info;
    this.handler = handler;
    this.entries = ImmutableList.of(info.player());
    this.packetAdapter = info.packetAdapter().createTeamDisplayAdapter(this);
    packetAdapter.updateTeamPackets();
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
    packetAdapter.updateTeamPackets();
    packetAdapter.sendProperties(PropertiesPacketType.UPDATE, handler.players());
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players) {
    handler.localeLineHandler()
      .sidebar()
      .packetAdapter()
      .sendScore(players, info.player(), info.objectiveScore(), null, info.scoreFormat());
  }

  @Override
  public void show(@NotNull Collection<Player> players) {
    sendScore(players);
    packetAdapter.sendProperties(PropertiesPacketType.CREATE, players);
  }

  @Override
  public void hide(@NotNull Collection<Player> players) {
    handler.localeLineHandler().sidebar().packetAdapter().removeScore(players, info.player());
    packetAdapter.removeTeam(players);
  }

  @Override
  public @NotNull Collection<String> syncedEntries() {
    return entries;
  }

  @Override
  public @NotNull Component displayName() {
    return empty();
  }

  @Override
  public @NotNull Component prefix() {
    Component value = info.value();
    return value == null ? empty() : value;
  }

  @Override
  public @NotNull Component suffix() {
    return empty();
  }
}
