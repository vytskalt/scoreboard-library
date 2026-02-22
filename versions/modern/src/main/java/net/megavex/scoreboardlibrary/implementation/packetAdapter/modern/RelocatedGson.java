package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import com.google.gson.JsonElement;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.MethodAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;

import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;

public final class RelocatedGson {
  public static final String SERVER_GSON_PKG = "com.go".concat("ogle.gson");
  public static final boolean IS_GSON_RELOCATED;
  public static final Object JSON_PARSER;
  public static final MethodAccessor PARSE_STRING_METHOD;

  private RelocatedGson() {
  }

  static {
    IS_GSON_RELOCATED = !JsonElement.class.getName().equals(SERVER_GSON_PKG + ".JsonElement");

    if (IS_GSON_RELOCATED) {
      try {
        Class<?> jsonElementClass = Class.forName(SERVER_GSON_PKG + ".JsonElement");
        Class<?> jsonParserClass = Class.forName(SERVER_GSON_PKG + ".JsonParser");
        JSON_PARSER = jsonParserClass.getConstructors()[0].newInstance();
        PARSE_STRING_METHOD = ReflectUtil.findMethod(jsonParserClass, false, MethodType.methodType(jsonElementClass, String.class), "parse");
      } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    } else {
      JSON_PARSER = null;
      PARSE_STRING_METHOD = null;
    }
    //System.out.println("GSON RELOCATED = " + IS_GSON_RELOCATED);
  }

  public static Object convertToServerGson(final JsonElement element) {
    if (IS_GSON_RELOCATED) {
      assert PARSE_STRING_METHOD != null;
      //System.out.println("fixed");
      return PARSE_STRING_METHOD.invoke(JSON_PARSER, element.toString());
    }
    return element;
  }
}
