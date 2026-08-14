package net.megavex.slib.sidebar;

public interface Sidebar<T> {
  void addViewer(final T viewer);

  void refresh();

  void refresh(final T viewer);

  void close();
}
