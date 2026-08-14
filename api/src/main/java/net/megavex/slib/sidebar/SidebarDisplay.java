package net.megavex.slib.sidebar;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SidebarDisplay {
  private final Component title;
  private final List<Line> lines;

  public SidebarDisplay(final Component title, final List<Line> lines) {
    this.title = title;
    this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
  }

  public Component getTitle() {
    return title;
  }

  public List<Line> getLines() {
    return lines;
  }

  public static final class Line {
    private final Component value;
    private final ScoreFormat scoreFormat;

    public Line(final Component value, final @Nullable ScoreFormat scoreFormat) {
      this.value = value;
      this.scoreFormat = scoreFormat;
    }

    public Line(final Component value) {
      this(value, null);
    }

    public Component getValue() {
      return value;
    }

    public ScoreFormat getScoreFormat() {
      return scoreFormat;
    }
  }
}
