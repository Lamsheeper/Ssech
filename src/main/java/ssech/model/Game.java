package ssech.model;

import java.util.Optional;
import java.util.Set;

/**
 * Click-driven game flow on top of a {@link BoardState}.
 *
 * <p>Clicking one of your troops selects it. Clicking a highlighted square then moves,
 * captures a troop or captures a flag, which ends the turn. A selected wizard can instead
 * click a swap target to trade places with it. A bandit that captures gets one follow-up
 * move of range 1; deselecting it during the follow-up passes the turn.
 */
public final class Game {
  private BoardState state;
  private Position selected;
  private Set<Position> moveTargets = Set.of();
  private Set<Position> swapTargets = Set.of();
  private boolean banditFollowUp;
  private boolean over;
  private Runnable onTurnEnd = () -> {};

  public Game(BoardState state) {
    this.state = state;
  }

  /** Called after every completed turn, including the one that ends the game. */
  public void setOnTurnEnd(Runnable onTurnEnd) {
    this.onTurnEnd = onTurnEnd;
  }

  public BoardState state() {
    return state;
  }

  public Optional<Position> selected() {
    return Optional.ofNullable(selected);
  }

  public Optional<Troop> selectedTroop() {
    return selected().map(p -> state.tile(p).troop());
  }

  public Set<Position> moveTargets() {
    return moveTargets;
  }

  public Set<Position> swapTargets() {
    return swapTargets;
  }

  public boolean isOver() {
    return over;
  }

  /** The winner once the game is over; empty while playing or on a tie. */
  public Optional<Team> winner() {
    int blue = state.score(Team.BLUE);
    int red = state.score(Team.RED);
    if (!over || blue == red) {
      return Optional.empty();
    }
    return Optional.of(blue > red ? Team.BLUE : Team.RED);
  }

  public void click(Position p) {
    if (over) {
      return;
    }
    Tile tile = state.tile(p);

    if (selected == null) {
      if (tile.isOccupied() && tile.troop().team() == state.turn()) {
        select(p);
      }
      return;
    }

    if (p.equals(selected)) {
      boolean passTurn = banditFollowUp;
      clearSelection();
      if (passTurn) {
        endTurn();
      }
      return;
    }

    if (moveTargets.contains(p)) {
      boolean banditCapture = tile.isOccupied()
          && !banditFollowUp
          && state.tile(selected).troop().type() == TroopType.BANDIT;
      if (tile.flag() != null) {
        Team capturer = state.tile(selected).troop().team();
        state.setScore(capturer, state.score(capturer) + 1);
        tile.setFlag(null);
      }
      moveTroop(selected, p);
      if (banditCapture) {
        selected = p;
        banditFollowUp = true;
        moveTargets = Rules.moveTargets(state, p, 1);
        swapTargets = Set.of();
      } else {
        clearSelection();
        endTurn();
      }
    } else if (swapTargets.contains(p)) {
      Tile from = state.tile(selected);
      Troop wizard = from.troop();
      from.setTroop(tile.troop());
      tile.setTroop(wizard);
      clearSelection();
      endTurn();
    }
  }

  /** Ends the game immediately; the higher score wins, equal scores tie. */
  public void forfeit() {
    clearSelection();
    over = true;
  }

  /** Resets to the starting layout of the current terrain. */
  public void restart() {
    state = Layouts.newGame(state.terrain());
    clearSelection();
    over = false;
  }

  private void select(Position p) {
    selected = p;
    moveTargets = Rules.moveTargets(state, p);
    swapTargets = state.tile(p).troop().type() == TroopType.WIZARD
        ? Rules.swapTargets(state, p, moveTargets)
        : Set.of();
  }

  private void clearSelection() {
    selected = null;
    moveTargets = Set.of();
    swapTargets = Set.of();
    banditFollowUp = false;
  }

  private void moveTroop(Position from, Position to) {
    Tile src = state.tile(from);
    Troop troop = src.troop();
    state.tile(to).setTroop(troop);
    src.setTroop(null);
    if (src.type() == TileType.CRACKED_BRIDGE && troop.type() != TroopType.FAIRY) {
      src.setType(TileType.RIVER);
    } else if (src.type() == TileType.ICE) {
      src.setIceDurability(Tile.FULL_ICE_DURABILITY);
    }
  }

  private void endTurn() {
    state.setTurn(state.turn().opponent());
    crackIce();
    if (state.score(Team.BLUE) >= Rules.WINNING_SCORE || state.score(Team.RED) >= Rules.WINNING_SCORE) {
      forfeit();
    }
    onTurnEnd.run();
  }

  private void crackIce() {
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 0; y < BoardState.HEIGHT; y++) {
        Tile tile = state.tile(x, y);
        if (tile.type() == TileType.ICE && tile.isOccupied() && tile.troop().type() != TroopType.FAIRY) {
          tile.setIceDurability(tile.iceDurability() - 1);
          if (tile.iceDurability() <= 0) {
            tile.setTroop(null);
            tile.setType(TileType.RIVER);
          }
        }
      }
    }
  }
}
