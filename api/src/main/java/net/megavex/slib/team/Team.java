package net.megavex.slib.team;

public interface Team<T> {
  String name();

  void refreshProperties();

  void refreshProperties(final T viewer);

  void refreshEntries();

  void refreshEntries(final T viewer);
}
