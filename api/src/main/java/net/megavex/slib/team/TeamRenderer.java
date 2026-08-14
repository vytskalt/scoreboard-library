package net.megavex.slib.team;

import java.util.Collection;

public interface TeamRenderer<T> {
  TeamProperties renderProperties(final T viewer);

  Collection<String> renderEntries(final T viewer);
}
