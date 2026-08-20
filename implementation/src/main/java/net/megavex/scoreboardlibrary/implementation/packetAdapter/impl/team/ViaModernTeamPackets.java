package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team;

import com.viaversion.viaversion.api.ViaAPI;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import org.bukkit.entity.Player;

public final class ViaModernTeamPackets {
  private ViaModernTeamPackets() {
  }

  public static void sendProperties(final ViaAPI<Player> via, final Player player,
                                    final PropertiesPacketType packetType, final ImmutableTeamProperties<Component> properties) {
    // todo
  }
}
