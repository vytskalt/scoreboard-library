package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.PaperObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.SpigotObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.SpigotTeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider {
  public PacketAdapterProviderImpl() {
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return ModernComponentProvider.IS_NATIVE_ADVENTURE
      ? new PaperObjectivePacketAdapter(objectiveName)
      : new SpigotObjectivePacketAdapter(objectiveName);
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return ModernComponentProvider.IS_NATIVE_ADVENTURE
      ? new PaperTeamsPacketAdapterImpl(teamName)
      : new SpigotTeamsPacketAdapter(teamName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    return LineRenderingStrategy.MODERN;
  }
}
