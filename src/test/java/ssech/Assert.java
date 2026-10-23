package ssech;

import java.util.Objects;

public final class Assert {
  private Assert() {}

  public static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  public static void assertFalse(boolean condition, String message) {
    assertTrue(!condition, message);
  }

  public static void assertEquals(Object expected, Object actual) {
    if (!Objects.equals(expected, actual)) {
      throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
    }
  }

  public static void assertThrows(Class<? extends Throwable> type, Runnable body) {
    try {
      body.run();
    } catch (Throwable t) {
      if (type.isInstance(t)) {
        return;
      }
      throw new AssertionError("expected " + type.getSimpleName() + " but got " + t, t);
    }
    throw new AssertionError("expected " + type.getSimpleName() + " to be thrown");
  }
}
