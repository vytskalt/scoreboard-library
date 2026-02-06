package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.PaperObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.SpigotObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.SpigotTeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.PacketUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketAdapterProviderImpl implements PacketAdapterProvider, PacketSender<Packet<?>> {
  private boolean isNativeAdventure;
  private final ComponentProvider componentProvider;

  public PacketAdapterProviderImpl() {
    try {
      Class.forName("io.papermc.paper.adventure.PaperAdventure");

      // Hide from relocation checkers
      String notRelocatedPackage = "net.ky".concat("ori.adventure.text");

      // The native adventure optimisations only work when the adventure library isn't relocated
      if (Component.class.getPackage().getName().equals(notRelocatedPackage)) {
        isNativeAdventure = true;
      }
    } catch (ClassNotFoundException ignored) {
    }

    this.componentProvider = new ComponentProviderImpl(isNativeAdventure);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return isNativeAdventure
      ? new PaperObjectivePacketAdapter(this, componentProvider, objectiveName)
      : new SpigotObjectivePacketAdapter(this, componentProvider, objectiveName);
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return isNativeAdventure
      ? new PaperTeamsPacketAdapterImpl(this, componentProvider, teamName)
      : new SpigotTeamsPacketAdapter(this, componentProvider, teamName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    return LineRenderingStrategy.MODERN;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
    PacketUtil.sendPacket(player, packet);
  }
}
