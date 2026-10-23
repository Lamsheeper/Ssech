package ssech.model;

/** The three maps a game can be played on. */
public enum Terrain {
  CLASSIC("Classic"),
  VOLCANIC("Volcanic"),
  TUNDRA("Tundra");

  private final String displayName;

  Terrain(String displayName) {
    this.displayName = displayName;
  }

  /** Name used in save files and as the asset folder name, e.g. {@code "Classic"}. */
  public String displayName() {
    return displayName;
  }

  public static Terrain fromDisplayName(String name) {
    for (Terrain t : values()) {
      if (t.displayName.equals(name)) {
        return t;
      }
    }
    throw new IllegalArgumentException("Unknown terrain: " + name);
  }
}
