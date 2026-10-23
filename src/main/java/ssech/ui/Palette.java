package ssech.ui;

import java.awt.Color;
import ssech.model.Team;
import ssech.model.Terrain;
import ssech.model.Tile;

/** Background colors drawn behind the (partly transparent) tile sprites. */
final class Palette {
  static final Color MOVE_HIGHLIGHT = new Color(215, 185, 0);
  static final Color DANGER_HIGHLIGHT = new Color(255, 100, 100);
  static final Color SIDEBAR = new Color(50, 50, 50);
  static final Color TIE = new Color(138, 43, 226);

  private Palette() {}

  static Color tile(Terrain terrain, Tile tile) {
    return switch (tile.type()) {
      case GROUND -> switch (terrain) {
        case CLASSIC -> new Color(0, 190, 104);
        case TUNDRA -> new Color(227, 234, 255);
        case VOLCANIC -> new Color(59, 26, 26);
      };
      case RIVER -> switch (terrain) {
        case CLASSIC -> new Color(64, 80, 210);
        case TUNDRA -> new Color(117, 168, 255);
        case VOLCANIC -> new Color(255, 81, 0);
      };
      case BRIDGE -> switch (terrain) {
        case CLASSIC -> new Color(190, 120, 25);
        case TUNDRA -> new Color(66, 66, 73);
        case VOLCANIC -> new Color(18, 0, 36);
      };
      case CRACKED_BRIDGE -> new Color(18, 0, 36);
      case ICE -> ice(tile.iceDurability());
    };
  }

  static Color swapHighlight(Team wizardTeam) {
    return wizardTeam == Team.RED ? new Color(255, 200, 200) : new Color(200, 200, 255);
  }

  static Color team(Team team) {
    return team == Team.RED ? Color.RED : Color.BLUE;
  }

  private static Color ice(int durability) {
    if (durability >= Tile.FULL_ICE_DURABILITY) {
      return new Color(117, 168, 255);
    }
    return new Color(81 + 6 * durability, 120 + 8 * durability, 183 + 12 * durability);
  }
}
