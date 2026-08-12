package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public final class PaperTeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public PaperTeamsPacketAdapterImpl(PacketAdapterProviderImpl provider, @NotNull String teamName) {
    super(provider, teamName);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    public TeamDisplayPacketAdapterImpl(@NotNull ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      Collection<String> entries = new ArrayList<>(properties.syncedEntries());

      Object displayName = PacketAccessors.fromAdventureComponent(properties.displayName());
      Object prefix = PacketAccessors.fromAdventureComponent(properties.prefix());
      Object suffix = PacketAccessors.fromAdventureComponent(properties.suffix());

      Object packet;
      if (PacketAccessors.IS_1_17_OR_ABOVE) {
        assert PacketAccessors.PARAMETERS_CONSTRUCTOR != null;
        Object parameters = PacketAccessors.createTeamParameters(displayName, prefix, suffix, properties);

        packet = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
          teamName,
          TeamConstants.mode(packetType),
          Optional.of(parameters),
          entries
        );
      } else {
        assert PacketAccessors.TEAM_NAME_FIELD != null;
        assert PacketAccessors.TEAM_MODE_FIELD != null;
        assert PacketAccessors.TEAM_ENTRIES_FIELD != null;

        packet = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
        PacketAccessors.TEAM_NAME_FIELD.set(packet, teamName);
        PacketAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
        PacketAccessors.TEAM_ENTRIES_FIELD.set(packet, entries);

        PacketAccessors.setupOldTeamPropertiesFields(packet, displayName, prefix, suffix, properties);
      }

      provider.packetSender().sendPacket(players, packet);
    }
  }
}
