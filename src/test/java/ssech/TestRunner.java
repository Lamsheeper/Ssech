package ssech;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import ssech.model.GameTest;
import ssech.model.RulesTest;
import ssech.persistence.SaveCodecTest;

/** Runs every {@link Test} method in the listed classes and exits non-zero on failure. */
public final class TestRunner {
  private static final List<Class<?>> SUITES = List.of(RulesTest.class, GameTest.class, SaveCodecTest.class);

  public static void main(String[] args) throws ReflectiveOperationException {
    int passed = 0;
    int failed = 0;
    for (Class<?> suite : SUITES) {
      for (Method method : suite.getDeclaredMethods()) {
        if (!method.isAnnotationPresent(Test.class)) {
          continue;
        }
        String name = suite.getSimpleName() + "." + method.getName();
        try {
          method.invoke(suite.getDeclaredConstructor().newInstance());
          passed++;
          System.out.println("PASS " + name);
        } catch (InvocationTargetException e) {
          failed++;
          System.out.println("FAIL " + name + ": " + e.getCause());
          e.getCause().printStackTrace(System.out);
        }
      }
    }
    System.out.printf("%n%d passed, %d failed%n", passed, failed);
    System.exit(failed == 0 ? 0 : 1);
  }
}
