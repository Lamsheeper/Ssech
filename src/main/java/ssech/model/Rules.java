package ssech.model;

import java.util.LinkedHashSet;
import java.util.Set;

/** Stateless movement rules. */
public final class Rules {
  public static final int WIZARD_SWAP_RANGE = 6;
  public static final int WINNING_SCORE = 2;

  private Rules() {}

  /**
   * Squares the troop at {@code from} may move onto within {@code range}: empty tiles,
   * enemy troops (capture) and enemy flags (capture). Rivers block everything but fairies.
   */
  public static Set<Position> moveTargets(BoardState state, Position from, int range) {
    Troop mover = state.tile(from).troop();
    Set<Position> targets = new LinkedHashSet<>();
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 0; y < BoardState.HEIGHT; y++) {
        Position to = new Position(x, y);
        if (from.distanceTo(to) <= range && canEnter(mover, state.tile(to))) {
          targets.add(to);
        }
      }
    }
    return targets;
  }

  public static Set<Position> moveTargets(BoardState state, Position from) {
    return moveTargets(state, from, state.tile(from).troop().type().travelRange());
  }

  /**
   * Troops a wizard at {@code from} can swap places with: any troop of either team within
   * {@link #WIZARD_SWAP_RANGE}, not on a river, and not already a capture target.
   */
  public static Set<Position> swapTargets(BoardState state, Position from, Set<Position> moveTargets) {
    Set<Position> targets = new LinkedHashSet<>();
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 0; y < BoardState.HEIGHT; y++) {
        Position to = new Position(x, y);
        Tile tile = state.tile(to);
        if (tile.isOccupied()
            && tile.type() != TileType.RIVER
            && !moveTargets.contains(to)
            && !to.equals(from)
            && from.distanceTo(to) <= WIZARD_SWAP_RANGE) {
          targets.add(to);
        }
      }
    }
    return targets;
  }

  static boolean canEnter(Troop mover, Tile tile) {
    if (mover.type() != TroopType.FAIRY && tile.type() == TileType.RIVER) {
      return false;
    }
    if (tile.isOccupied()) {
      return tile.troop().team() != mover.team();
    }
    return tile.flag() != mover.team();
  }
}
