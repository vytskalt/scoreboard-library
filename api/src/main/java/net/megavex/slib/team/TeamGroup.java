package net.megavex.slib.team;

public interface TeamGroup<T> {
  void addViewer(final T viewer);

  void removeViewer(final T viewer);

  Team<T> createTeam(final String name, final TeamRenderer<T> renderer);

  void removeTeam(final String name);

  void close();
}
