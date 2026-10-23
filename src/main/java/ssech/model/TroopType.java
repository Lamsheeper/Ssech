package ssech.model;

/**
 * Kinds of troops. Special abilities live in {@link Rules} and {@link Game}:
 * fairies cross rivers, wizards swap places with nearby troops, and bandits
 * get a one-tile follow-up move after a capture.
 */
public enum TroopType {
  KNIGHT("Knight", 'K', 1),
  PRINCE("Prince", 'P', 2),
  WIZARD("Wizard", 'W', 1),
  BANDIT("Bandit", 'B', 2),
  FAIRY("Fairy", 'F', 1);

  private final String displayName;
  private final char letter;
  private final int travelRange;

  TroopType(String displayName, char letter, int travelRange) {
    this.displayName = displayName;
    this.letter = letter;
    this.travelRange = travelRange;
  }

  /** Name used in save files and sprite names, e.g. {@code "Knight"}. */
  public String displayName() {
    return displayName;
  }

  /** Single-letter code used for description asset names, e.g. {@code 'K'}. */
  public char letter() {
    return letter;
  }

  /** Maximum Manhattan distance this troop can move in one turn. */
  public int travelRange() {
    return travelRange;
  }

  public static TroopType fromDisplayName(String name) {
    for (TroopType t : values()) {
      if (t.displayName.equals(name)) {
        return t;
      }
    }
    throw new IllegalArgumentException("Unknown troop type: " + name);
  }
}
