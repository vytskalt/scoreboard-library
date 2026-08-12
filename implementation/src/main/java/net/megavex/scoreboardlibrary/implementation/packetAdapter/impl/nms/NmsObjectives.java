package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;

import java.util.Objects;
import java.util.Optional;

import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.*;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.COMPONENT_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.DISPLAY_SLOT_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.IS_1_13_OR_ABOVE;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.IS_1_17_OR_ABOVE;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.IS_1_20_2_OR_ABOVE;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.IS_1_20_3_OR_ABOVE;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.IS_1_8_OR_ABOVE;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.NUMBER_FORMAT_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.OBJECTIVE_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.SCORE_ACTION_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.SET_DISPLAY_OBJECTIVE_PKT_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.SET_OBJECTIVE_PKT_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.SET_SCORE_PKT_CLASS;

public final class NmsObjectives {
  private NmsObjectives() {
  }

  // --- SCORE PACKETS ---

  public static final ConstructorAccessor<?> RESET_SCORE_CONSTRUCTOR; // 1.20.3+

  static {
    if (IS_1_20_3_OR_ABOVE) {
      RESET_SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(Objects.requireNonNull(RESET_SCORE_PKT_CLASS), String.class, String.class);
    } else {
      RESET_SCORE_CONSTRUCTOR = null;
    }
  }

  public static final ConstructorAccessor<?> SCORE_CONSTRUCTOR;

  static {
    if (IS_1_20_5_OR_ABOVE) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, Optional.class);
      SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, String.class, String.class, int.class, Optional.class, Optional.class);
    } else if (IS_1_20_3_OR_ABOVE) {
      assert NUMBER_FORMAT_CLASS != null;
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, NUMBER_FORMAT_CLASS);
      SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, String.class, String.class, int.class, COMPONENT_CLASS, NUMBER_FORMAT_CLASS);
    } else if (IS_1_13_OR_ABOVE) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = null;
      SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, SCORE_ACTION_CLASS, String.class, String.class, int.class);
    } else {
      OBJECTIVE_NUMBER_FORMAT_FIELD = null;
      SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, String.class);
    }
  }

  public static final FieldAccessor<Object, String> SCORE_OBJECTIVE_NAME_FIELD;
  public static final FieldAccessor<Object, Integer> SCORE_VALUE_FIELD;
  public static final FieldAccessor<Object, Object> SCORE_ACTION_FIELD;

  static {
    if (!IS_1_13_OR_ABOVE) {
      SCORE_OBJECTIVE_NAME_FIELD = ReflectUtil.findFieldUnchecked(SET_SCORE_PKT_CLASS, 1, String.class);
      SCORE_VALUE_FIELD = ReflectUtil.findFieldUnchecked(SET_SCORE_PKT_CLASS, 0, int.class);
      if (IS_1_8_OR_ABOVE) {
        SCORE_ACTION_FIELD = ReflectUtil.findFieldUnchecked(SET_SCORE_PKT_CLASS, 0, SCORE_ACTION_CLASS);
      } else {
        SCORE_ACTION_FIELD = ReflectUtil.findFieldUnchecked(SET_SCORE_PKT_CLASS, 1, int.class);
      }
    } else {
      SCORE_OBJECTIVE_NAME_FIELD = null;
      SCORE_VALUE_FIELD = null;
      SCORE_ACTION_FIELD = null;
    }
  }

  // --- OBJECTIVES ---

  public static final ConstructorAccessor<?> OBJECTIVE_PACKET_CONSTRUCTOR;
  public static final FieldAccessor<Object, String> OBJECTIVE_NAME_FIELD;
  public static final FieldAccessor<Object, Object> OBJECTIVE_VALUE_FIELD;
  public static final FieldAccessor<Object, Object> OBJECTIVE_RENDER_TYPE_FIELD;
  public static final FieldAccessor<Object, Object> OBJECTIVE_NUMBER_FORMAT_FIELD; // Optional<NumberFormat> for 1.20.5+, NumberFormat for below
  public static final FieldAccessor<Object, Integer> OBJECTIVE_MODE_FIELD;

  public static final ConstructorAccessor<?> DISPLAY_CONSTRUCTOR;
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME;
  public static final FieldAccessor<Object, Object> DISPLAY_SLOT;

  static {
    OBJECTIVE_PACKET_CONSTRUCTOR = ReflectUtil.getEmptyConstructor(SET_OBJECTIVE_PKT_CLASS);
    OBJECTIVE_NAME_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, String.class);
    OBJECTIVE_VALUE_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, IS_1_13_OR_ABOVE ? 0 : 1, IS_1_13_OR_ABOVE ? COMPONENT_CLASS : String.class);

    if (IS_1_8_OR_ABOVE) {
      OBJECTIVE_RENDER_TYPE_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS);
    } else {
      OBJECTIVE_RENDER_TYPE_FIELD = null;
    }

    OBJECTIVE_MODE_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, int.class);

    if (IS_1_20_2_OR_ABOVE) {
      assert DISPLAY_SLOT_CLASS != null;
      DISPLAY_CONSTRUCTOR = ReflectUtil.findConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS, DISPLAY_SLOT_CLASS, OBJECTIVE_CLASS);
    } else if (IS_1_17_OR_ABOVE) {
      DISPLAY_CONSTRUCTOR = ReflectUtil.findConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS, int.class, OBJECTIVE_CLASS);
    } else {
      DISPLAY_CONSTRUCTOR = ReflectUtil.findConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS);
    }

    DISPLAY_OBJECTIVE_NAME = ReflectUtil.findFieldUnchecked(SET_DISPLAY_OBJECTIVE_PKT_CLASS, 0, String.class);

    if (!IS_1_17_OR_ABOVE) {
      DISPLAY_SLOT = ReflectUtil.findFieldUnchecked(SET_DISPLAY_OBJECTIVE_PKT_CLASS, 0, int.class);
    } else {
      DISPLAY_SLOT = null;
    }
  }
}
