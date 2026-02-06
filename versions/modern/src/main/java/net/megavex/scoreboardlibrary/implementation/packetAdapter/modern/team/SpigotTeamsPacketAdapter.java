package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Locale;

public class SpigotTeamsPacketAdapter extends AbstractTeamsPacketAdapterImpl {
  public SpigotTeamsPacketAdapter(@NotNull PacketSender<Packet<?>> sender, @NotNull ComponentProvider componentProvider, @NotNull String teamName) {
    super(sender, componentProvider, teamName);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      Collection<String> entries = ImmutableList.copyOf(properties.syncedEntries());
      LocalePacketUtil.sendLocalePackets(
        sender,
        players,
        locale -> {
          ClientboundSetPlayerTeamPacket.@NotNull Parameters parameters = PacketAccessors.PARAMETERS_CONSTRUCTOR.invoke();
          fillParameters(parameters, locale);
          return createTeamsPacket(TeamConstants.mode(packetType), teamName, parameters, entries);
        }
      );
    }

    @Override
    protected void fillParameters(ClientboundSetPlayerTeamPacket.@NotNull Parameters parameters, @UnknownNullability Locale locale) {
      super.fillParameters(parameters, locale);

      PacketAccessors.DISPLAY_NAME_FIELD.set(parameters, componentProvider.fromAdventure(properties.displayName(), locale));
      PacketAccessors.PREFIX_FIELD.set(parameters, componentProvider.fromAdventure(properties.prefix(), locale));
      PacketAccessors.SUFFIX_FIELD.set(parameters, componentProvider.fromAdventure(properties.suffix(), locale));
    }
  }
}
