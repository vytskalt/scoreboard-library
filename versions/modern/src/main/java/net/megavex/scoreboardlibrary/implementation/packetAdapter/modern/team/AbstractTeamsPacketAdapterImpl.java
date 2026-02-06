package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters;
import net.minecraft.world.scores.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public abstract class AbstractTeamsPacketAdapterImpl implements TeamsPacketAdapter {
  protected final PacketSender<Packet<?>> sender;
  protected final ComponentProvider componentProvider;
  protected final String teamName;
  private ClientboundSetPlayerTeamPacket removePacket;

  public AbstractTeamsPacketAdapterImpl(@NotNull PacketSender<Packet<?>> sender, @NotNull ComponentProvider componentProvider, @NotNull String teamName) {
    this.sender = sender;
    this.componentProvider = componentProvider;
    this.teamName = teamName;
  }

  public static ClientboundSetPlayerTeamPacket createTeamsPacket(
    int method,
    @NotNull String name,
    @Nullable Parameters parameters,
    @Nullable Collection<String> entries
  ) {
    return PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
      name,
      method,
      Optional.ofNullable(parameters),
      entries == null ? Collections.emptyList() : entries
    );
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = createTeamsPacket(TeamConstants.MODE_REMOVE, teamName, null, null);
    }
    sender.sendPacket(players, removePacket);
  }

  public abstract class TeamDisplayPacketAdapterImpl implements TeamDisplayPacketAdapter {
    protected final ImmutableTeamProperties<Component> properties;

    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      this.properties = properties;
    }

    @Override
    public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      sender.sendPacket(players, createTeamsPacket(TeamConstants.mode(packetType), teamName, null, entries));
    }

    protected void fillParameters(@NotNull Parameters parameters, @UnknownNullability Locale locale) {
      if (PacketAccessors.IS_1_21_5_OR_ABOVE) {
        Objects.requireNonNull(PacketAccessors.NAME_TAG_VISIBILITY_FIELD_1_21_5)
          .set(parameters, Team.Visibility.valueOf(properties.nameTagVisibility().name()));

        Objects.requireNonNull(PacketAccessors.COLLISION_RULE_FIELD_1_21_5)
          .set(parameters, Team.CollisionRule.valueOf(properties.collisionRule().name()));
      } else {
        Objects.requireNonNull(PacketAccessors.NAME_TAG_VISIBILITY_FIELD_1_21_4)
          .set(parameters, properties.nameTagVisibility().key());

        Objects.requireNonNull(PacketAccessors.COLLISION_RULE_FIELD_1_21_4)
          .set(parameters, properties.collisionRule().key());
      }

      char legacyChar = LegacyFormatUtil.getChar(properties.playerColor());
      PacketAccessors.COLOR_FIELD.set(parameters, ChatFormatting.getByCode(legacyChar));

      int options = properties.packOptions();
      PacketAccessors.OPTIONS_FIELD.set(parameters, options);
    }
  }
}
