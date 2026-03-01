package net.megavex.scoreboardlibrary.implementation.packetAdapter.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TeamsPacketAdapter {
  @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties);

  default @NotNull TeamDisplayPacketAdapter createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    throw new UnsupportedOperationException();
  }
}
