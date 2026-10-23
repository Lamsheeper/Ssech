package ssech.model;

/** The two sides. Blue starts at the top of the board, red at the bottom; red moves first. */
public enum Team {
  BLUE(0, "blue"),
  RED(1, "red");

  private final int index;
  private final String id;

  Team(int index, String id) {
    this.index = index;
    this.id = id;
  }

  /** Numeric id used in save files and asset names (0 = blue, 1 = red). */
  public int index() {
    return index;
  }

  /** Lowercase name used in asset file names, e.g. {@code "blue"}. */
  public String id() {
    return id;
  }

  public Team opponent() {
    return this == BLUE ? RED : BLUE;
  }

  public static Team fromIndex(int index) {
    for (Team t : values()) {
      if (t.index == index) {
        return t;
      }
    }
    throw new IllegalArgumentException("Unknown team index: " + index);
  }
}
