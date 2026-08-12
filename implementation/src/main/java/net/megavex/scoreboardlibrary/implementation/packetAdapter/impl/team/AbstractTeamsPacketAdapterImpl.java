package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.NmsAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public abstract class AbstractTeamsPacketAdapterImpl implements TeamsPacketAdapter {
  protected final PacketAdapterProviderImpl provider;
  protected final String teamName;
  private Object removePacket;

  public AbstractTeamsPacketAdapterImpl(@NotNull PacketAdapterProviderImpl provider, @NotNull String teamName) {
    this.provider = provider;
    this.teamName = teamName;
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    return new LegacyTeamDisplayPacketAdapter(provider, teamName, properties);
  }

  public abstract class TeamDisplayPacketAdapterImpl implements TeamDisplayPacketAdapter {
    protected final ImmutableTeamProperties<Component> properties;

    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      this.properties = properties;
    }

    @Override
    public void removeTeam(@NotNull Iterable<Player> players) {
      if (removePacket == null) {
        if (NmsAccessors.IS_1_17_OR_ABOVE) {
          removePacket = NmsAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
            teamName,
            TeamConstants.MODE_REMOVE,
            null,
            Collections.emptyList()
          );
        } else {
          assert NmsAccessors.TEAM_NAME_FIELD != null;
          assert NmsAccessors.TEAM_MODE_FIELD != null;

          removePacket = NmsAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
          NmsAccessors.TEAM_NAME_FIELD.set(removePacket, teamName);
          NmsAccessors.TEAM_MODE_FIELD.set(removePacket, TeamConstants.MODE_REMOVE);
        }
      }
      provider.packetSender().sendPacket(players, removePacket);
    }

    @Override
    public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      if (NmsAccessors.IS_1_17_OR_ABOVE) {
        Object packet = NmsAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
          teamName,
          TeamConstants.mode(packetType),
          Optional.empty(),
          entries
        );
        provider.packetSender().sendPacket(players, packet);
      } else {
        assert NmsAccessors.TEAM_NAME_FIELD != null;
        assert NmsAccessors.TEAM_MODE_FIELD != null;
        assert NmsAccessors.TEAM_ENTRIES_FIELD != null;

        Object packet = NmsAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
        NmsAccessors.TEAM_NAME_FIELD.set(packet, teamName);
        NmsAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
        NmsAccessors.TEAM_ENTRIES_FIELD.set(packet, entries);
        provider.packetSender().sendPacket(players, packet);
      }
    }
  }
}
