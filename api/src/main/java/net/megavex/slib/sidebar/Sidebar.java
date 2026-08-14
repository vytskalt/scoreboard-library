package net.megavex.slib.sidebar;

public interface Sidebar<T> {
  int VANILLA_MAX_LINES = 15;

  void addViewer(final T viewer);

  void removeViewer(final T viewer);

  void refresh();

  void refresh(final T viewer);

  void close();
}
