package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsTeams;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses;
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
      if (NmsClasses.IS_1_17_OR_ABOVE) {
        assert NmsTeams.PARAMETERS_CONSTRUCTOR != null;
        Object parameters = NmsTeams.createTeamParameters(displayName, prefix, suffix, properties);

        packet = NmsTeams.TEAM_PACKET_CONSTRUCTOR.invoke(
          teamName,
          TeamConstants.mode(packetType),
          Optional.of(parameters),
          entries
        );
      } else {
        assert NmsTeams.TEAM_NAME_FIELD != null;
        assert NmsTeams.TEAM_MODE_FIELD != null;
        assert NmsTeams.TEAM_ENTRIES_FIELD != null;

        packet = NmsTeams.TEAM_PACKET_CONSTRUCTOR.invoke();
        NmsTeams.TEAM_NAME_FIELD.set(packet, teamName);
        NmsTeams.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
        NmsTeams.TEAM_ENTRIES_FIELD.set(packet, entries);

        NmsTeams.setupOldTeamPropertiesFields(packet, displayName, prefix, suffix, properties);
      }

      provider.packetSender().sendPacket(players, packet);
    }
  }
}
