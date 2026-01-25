package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Locale;

public class PaperTeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public PaperTeamsPacketAdapterImpl(@NotNull String teamName) {
    super(teamName);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    private final Object parameters = PacketAccessors.PARAMETERS_CONSTRUCTOR.invoke();
    private Object createPacket = null;
    private Object updatePacket = null;
    private Component displayName, prefix, suffix;

    public TeamDisplayPacketAdapterImpl(@NotNull ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void updateTeamPackets() {
      fillParameters(parameters, null);
      createPacket = null;
      updatePacket = null;
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      if (createPacket == null || updatePacket == null) {
        Collection<String> entries = ImmutableList.copyOf(properties.syncedEntries());
        createPacket = createTeamsPacket(TeamConstants.MODE_CREATE, teamName, parameters, entries);
        updatePacket = createTeamsPacket(TeamConstants.MODE_UPDATE, teamName, parameters, entries);
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

      if (properties.displayName() != displayName) {
        Object vanilla = PacketAccessors.fromAdventureComponent(properties.displayName());
        PacketAccessors.DISPLAY_NAME_FIELD.set(parameters, vanilla);
        displayName = properties.displayName();
      }

      if (properties.prefix() != prefix) {
        Object vanilla = PacketAccessors.fromAdventureComponent(properties.prefix());
        PacketAccessors.PREFIX_FIELD.set(parameters, vanilla);
        prefix = properties.prefix();
      }

      if (properties.suffix() != suffix) {
        Object vanilla = PacketAccessors.fromAdventureComponent(properties.suffix());
        PacketAccessors.SUFFIX_FIELD.set(parameters, vanilla);
        suffix = properties.suffix();
      }
    }
  }
}
