package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.NmsAccessors;
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

      Object displayName = NmsAccessors.fromAdventureComponent(properties.displayName());
      Object prefix = NmsAccessors.fromAdventureComponent(properties.prefix());
      Object suffix = NmsAccessors.fromAdventureComponent(properties.suffix());

      Object packet;
      if (NmsAccessors.IS_1_17_OR_ABOVE) {
        assert NmsAccessors.PARAMETERS_CONSTRUCTOR != null;
        Object parameters = NmsAccessors.createTeamParameters(displayName, prefix, suffix, properties);

        packet = NmsAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
          teamName,
          TeamConstants.mode(packetType),
          Optional.of(parameters),
          entries
        );
      } else {
        assert NmsAccessors.TEAM_NAME_FIELD != null;
        assert NmsAccessors.TEAM_MODE_FIELD != null;
        assert NmsAccessors.TEAM_ENTRIES_FIELD != null;

        packet = NmsAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
        NmsAccessors.TEAM_NAME_FIELD.set(packet, teamName);
        NmsAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
        NmsAccessors.TEAM_ENTRIES_FIELD.set(packet, entries);

        NmsAccessors.setupOldTeamPropertiesFields(packet, displayName, prefix, suffix, properties);
      }

      provider.packetSender().sendPacket(players, packet);
    }
  }
}
