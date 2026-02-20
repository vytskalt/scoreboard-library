package net.megavex.scoreboardlibrary.implementation.packetAdapter.team;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface TeamDisplayPacketAdapter {
  void removeTeam(@NotNull Iterable<Player> players);

  default void updateTeamPackets() {
  }

  void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries);

  void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players);
}
