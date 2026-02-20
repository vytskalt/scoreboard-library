package net.megavex.scoreboardlibrary.implementation.sidebar.line.locale;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.SidebarLineHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

// Implementation for versions below 1.13
public class LegacyLocaleLine implements LocaleLine, ImmutableTeamProperties<String> {
  private final GlobalLineInfo info;
  private final SidebarLineHandler handler;
  private final TeamDisplayPacketAdapter packetAdapter;
  private String player, oldPlayer;
  private String prefix, suffix;
  private String currentValue;

  public LegacyLocaleLine(GlobalLineInfo info, SidebarLineHandler handler) {
    this.info = info;
    this.handler = handler;
    this.player = info.player();
    this.packetAdapter = info.packetAdapter().createLegacyTeamDisplayAdapter(this);
    packetAdapter.updateTeamPackets();
  }

  public @NotNull GlobalLineInfo info() {
    return info;
  }

  @Override
  public @NotNull String displayName() {
    return "";
  }

  @Override
  public @NotNull String prefix() {
    return prefix;
  }

  @Override
  public @NotNull String suffix() {
    return suffix;
  }

  @Override
  public @NotNull Collection<String> syncedEntries() {
    return ImmutableList.of(player);
  }

  @Override
  public void value(@NotNull Component renderedComponent) {
    // I really need to rewrite this
    String legacyValue = legacySection().serialize(renderedComponent);

    String prevPlayer = player;

    if (legacyValue.length() <= 16) {
      this.prefix = legacyValue;
      this.suffix = "";

      if (this.currentValue != null && this.currentValue.length() > 32) {
        this.player = info.player();
      }
    } else {
      boolean endsWithSection = legacyValue.charAt(15) == LegacyComponentSerializer.SECTION_CHAR;

      int prefixEnd = endsWithSection ? 15 : 16;
      this.prefix = legacyValue.substring(0, prefixEnd);

      String last = prefix + LegacyComponentSerializer.SECTION_CHAR + (endsWithSection ? legacyValue.charAt(16) : "");
      this.player = info.player() + ChatColor.getLastColors(last);

      int playerEnd = prefixEnd;
      if (legacyValue.length() > 32) {
        int remaining = 16 - player.length();
        assert remaining > 0;

        playerEnd += remaining;
        player += legacyValue.substring(prefixEnd, playerEnd);
      }

      this.suffix = ChatColor.getLastColors(this.player) + legacyValue.substring(playerEnd + (endsWithSection ? 2 : 0));
      if (suffix.length() > 16) {
        String newSuffix = suffix.substring(0, 16);
        if (newSuffix.endsWith(String.valueOf(LegacyComponentSerializer.SECTION_CHAR))) {
          newSuffix = newSuffix.substring(0, 15);
        }

        suffix = newSuffix;
      }
    }

    currentValue = legacyValue;

    if (!player.equals(prevPlayer)) {
      oldPlayer = prevPlayer;
    }
  }

  @Override
  public void updateTeam() {
    Set<Player> players = handler.players();
    if (oldPlayer != null) {
      ObjectivePacketAdapter pd = handler.localeLineHandler().sidebar().packetAdapter();

      packetAdapter.sendEntries(EntriesPacketType.REMOVE, players, Collections.singleton(oldPlayer));
      pd.removeScore(players, oldPlayer);
      oldPlayer = null;

      packetAdapter.sendEntries(EntriesPacketType.ADD, players, Collections.singleton(player));
      pd.sendScore(players, player, info.objectiveScore(), null, null);
    }

    packetAdapter.sendProperties(PropertiesPacketType.UPDATE, players);
  }

  @Override
  public void resetOldPlayer() {
    oldPlayer = null;
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players) {
    ObjectivePacketAdapter pd = handler.localeLineHandler().sidebar().packetAdapter();
    if (oldPlayer != null) {
      pd.removeScore(players, oldPlayer);
    }

    pd.sendScore(players, player, info.objectiveScore(), null, null);
  }

  @Override
  public void show(@NotNull Collection<Player> players) {
    sendScore(players);
    packetAdapter.sendProperties(PropertiesPacketType.CREATE, players);
  }

  @Override
  public void hide(@NotNull Collection<Player> players) {
    ObjectivePacketAdapter pd = handler.localeLineHandler().sidebar().packetAdapter();
    if (oldPlayer != null) {
      pd.removeScore(players, oldPlayer);
    }

    pd.removeScore(players, player);
    packetAdapter.removeTeam(players);
  }
}

