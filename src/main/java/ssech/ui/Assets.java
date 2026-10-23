package ssech.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/** Loads and caches images bundled under {@code /assets} on the classpath. */
public final class Assets {
  private static final Map<String, ImageIcon> CACHE = new HashMap<>();

  private Assets() {}

  /** @param path path relative to the assets root, e.g. {@code "Misc/titlescreen.png"} */
  public static ImageIcon icon(String path) {
    return CACHE.computeIfAbsent(path, Assets::load);
  }

  private static ImageIcon load(String path) {
    URL url = Assets.class.getResource("/assets/" + path);
    if (url == null) {
      throw new IllegalStateException("Missing asset: " + path);
    }
    return new ImageIcon(url);
  }
}
