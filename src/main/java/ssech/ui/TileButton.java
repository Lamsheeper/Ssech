package ssech.ui;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import ssech.model.Game;
import ssech.model.Position;
import ssech.model.Team;
import ssech.model.Terrain;
import ssech.model.Tile;
import ssech.model.TileType;
import ssech.model.Troop;

/** One square of the board. Holds no game state; {@link #render} redraws it from the model. */
final class TileButton extends JButton {
  private final Position position;

  TileButton(Position position) {
    this.position = position;
    setMargin(new Insets(0, 0, 0, 0));
    setFocusPainted(false);
  }

  Position position() {
    return position;
  }

  void render(Game game) {
    Terrain terrain = game.state().terrain();
    Tile tile = game.state().tile(position);
    boolean isSelected = game.selected().map(position::equals).orElse(false);
    boolean isMoveTarget = game.moveTargets().contains(position);
    boolean isSwapTarget = game.swapTargets().contains(position);
    boolean bareTile = !tile.isOccupied() && tile.flag() == null;

    if (game.isOver() && bareTile) {
      setIcon(null);
    } else if (isMoveTarget && bareTile) {
      setIcon(null);
    } else {
      setIcon(icon(terrain, tile));
    }

    if (isSelected || isMoveTarget) {
      boolean hazardous = tile.type() == TileType.CRACKED_BRIDGE || tile.type() == TileType.ICE;
      setBackground(hazardous ? Palette.DANGER_HIGHLIGHT : Palette.MOVE_HIGHLIGHT);
    } else if (isSwapTarget) {
      setBackground(Palette.swapHighlight(game.selectedTroop().orElseThrow().team()));
    } else {
      setBackground(Palette.tile(terrain, tile));
    }

    if (game.isOver()) {
      Color color = game.winner().map(Palette::team).orElse(Palette.TIE);
      setBorder(new LineBorder(color));
    } else {
      setBorder(UIManager.getBorder("Button.border"));
    }
    setBorderPainted(tile.type() != TileType.RIVER);
    setEnabled(!game.isOver());
  }

  private static Icon icon(Terrain terrain, Tile tile) {
    Troop troop = tile.troop();
    if (troop != null) {
      return Assets.icon("TroopPNGs/" + troop.team().id() + troop.type().displayName() + ".png");
    }
    Team flag = tile.flag();
    if (flag != null) {
      return Assets.icon("Terrain/" + flag.id() + "Flag.png");
    }
    String folder = terrain.displayName();
    return switch (tile.type()) {
      case GROUND -> Assets.icon(folder + "/ground.png");
      case RIVER -> Assets.icon(folder + "/liquid.png");
      case BRIDGE -> Assets.icon(folder + "/bridge.png");
      case CRACKED_BRIDGE -> Assets.icon("Volcanic/cracked.png");
      case ICE -> Assets.icon("Tundra/ice.png");
    };
  }
}
