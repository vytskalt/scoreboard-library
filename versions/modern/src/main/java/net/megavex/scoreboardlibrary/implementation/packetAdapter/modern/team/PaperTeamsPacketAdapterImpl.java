package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class PaperTeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public PaperTeamsPacketAdapterImpl(PacketAdapterProviderImpl provider, @NotNull String teamName) {
    super(provider, teamName);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    private final Object parameters;
    private Object createPacket = null;
    private Object updatePacket = null;

    public TeamDisplayPacketAdapterImpl(@NotNull ImmutableTeamProperties<Component> properties) {
      super(properties);
      if (PacketAccessors.IS_1_17_OR_ABOVE) {
        this.parameters = PacketAccessors.PARAMETERS_CONSTRUCTOR.invoke();
      } else {
        this.parameters = null;
      }
    }

    @Override
    public void updateTeamPackets() {
      if (parameters != null) {
        fillParameters(parameters, null);
      }
      createPacket = null;
      updatePacket = null;
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      if (createPacket == null || updatePacket == null) {
        Collection<String> entries = ImmutableList.copyOf(properties.syncedEntries());
        if (parameters != null) {
          createPacket = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
            teamName,
            TeamConstants.MODE_CREATE,
            Optional.of(parameters),
            entries
          );

          updatePacket = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
            teamName,
            TeamConstants.MODE_UPDATE,
            Optional.of(parameters),
            entries
          );
        } else {
          assert PacketAccessors.TEAM_NAME_FIELD != null;
          assert PacketAccessors.TEAM_MODE_FIELD != null;

          createPacket = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
          PacketAccessors.TEAM_NAME_FIELD.set(createPacket, teamName);
          PacketAccessors.TEAM_MODE_FIELD.set(createPacket, TeamConstants.MODE_CREATE);
          PacketAccessors.TEAM_ENTRIES_FIELD.set(createPacket, entries);
          fillParameters(createPacket, null);

          updatePacket = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
          PacketAccessors.TEAM_NAME_FIELD.set(updatePacket, teamName);
          PacketAccessors.TEAM_MODE_FIELD.set(updatePacket, TeamConstants.MODE_UPDATE);
          PacketAccessors.TEAM_ENTRIES_FIELD.set(updatePacket, entries);
          fillParameters(updatePacket, null);
        }
      }

      switch (packetType) {
        case CREATE:
          ModernPacketSender.INSTANCE.sendPacket(players, createPacket);
          break;
        case UPDATE:
          ModernPacketSender.INSTANCE.sendPacket(players, updatePacket);
          break;
      }
    }

    @Override
    protected void fillParameters(@NotNull Object parameters, @UnknownNullability Locale locale) {
      super.fillParameters(parameters, locale);
      PacketAccessors.DISPLAY_NAME_FIELD.set(parameters, PacketAccessors.fromAdventureComponent(properties.displayName()));
      PacketAccessors.PREFIX_FIELD.set(parameters, PacketAccessors.fromAdventureComponent(properties.prefix()));
      PacketAccessors.SUFFIX_FIELD.set(parameters, PacketAccessors.fromAdventureComponent(properties.suffix()));
    }
  }
}
