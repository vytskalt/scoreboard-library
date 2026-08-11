package net.megavex.scoreboardlibrary.tests.api.animation;

import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.tests.Assert;

import java.util.Arrays;
import java.util.List;

public final class CollectionAnimationTest {
  private CollectionAnimationTest() {
  }

  public static void loopTest() {
    List<Integer> frames = Arrays.asList(0, 1, 2);
    CollectionSidebarAnimation<Integer> animation = new CollectionSidebarAnimation<>(frames);
    Assert.equals(0, animation.currentFrame());
    animation.nextFrame();
    Assert.equals(1, animation.currentFrame());
    animation.nextFrame();
    Assert.equals(2, animation.currentFrame());
    animation.nextFrame();
    Assert.equals(0, animation.currentFrame());
  }
}
