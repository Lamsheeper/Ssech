package ssech.model;

import static ssech.Assert.assertEquals;
import static ssech.Assert.assertFalse;
import static ssech.Assert.assertTrue;

import java.util.Set;
import ssech.Test;

public class RulesTest {

  static BoardState emptyBoard(Terrain terrain) {
    return new BoardState(terrain);
  }

  static void put(BoardState s, int x, int y, TroopType type, Team team) {
    s.tile(x, y).setTroop(new Troop(type, team));
  }

  @Test
  public void rangeOneTroopsReachFourNeighbors() {
    for (TroopType type : new TroopType[] {TroopType.KNIGHT, TroopType.WIZARD, TroopType.FAIRY}) {
      BoardState s = emptyBoard(Terrain.CLASSIC);
      put(s, 4, 4, type, Team.RED);
      Set<Position> targets = Rules.moveTargets(s, new Position(4, 4));
      assertEquals(Set.of(new Position(3, 4), new Position(5, 4), new Position(4, 3), new Position(4, 5)), targets);
    }
  }

  @Test
  public void rangeTwoTroopsReachDiamond() {
    for (TroopType type : new TroopType[] {TroopType.PRINCE, TroopType.BANDIT}) {
      BoardState s = emptyBoard(Terrain.CLASSIC);
      put(s, 4, 4, type, Team.RED);
      Set<Position> targets = Rules.moveTargets(s, new Position(4, 4));
      assertEquals(12, targets.size());
      assertTrue(targets.contains(new Position(4, 6)), "two straight");
      assertTrue(targets.contains(new Position(5, 5)), "diagonal");
      assertFalse(targets.contains(new Position(5, 6)), "distance three");
    }
  }

  @Test
  public void edgesClipTargets() {
    BoardState s = emptyBoard(Terrain.CLASSIC);
    put(s, 0, 0, TroopType.KNIGHT, Team.BLUE);
    assertEquals(Set.of(new Position(1, 0), new Position(0, 1)), Rules.moveTargets(s, new Position(0, 0)));
  }

  @Test
  public void riversBlockAllButFairies() {
    BoardState s = emptyBoard(Terrain.CLASSIC);
    s.tile(4, 5).setType(TileType.RIVER);
    put(s, 4, 4, TroopType.KNIGHT, Team.RED);
    assertFalse(Rules.moveTargets(s, new Position(4, 4)).contains(new Position(4, 5)), "knight crossed river");

    put(s, 4, 4, TroopType.FAIRY, Team.RED);
    assertTrue(Rules.moveTargets(s, new Position(4, 4)).contains(new Position(4, 5)), "fairy blocked by river");
  }

  @Test
  public void cannotLandOnFriendlyTroopsOrOwnFlags() {
    BoardState s = emptyBoard(Terrain.CLASSIC);
    put(s, 4, 4, TroopType.KNIGHT, Team.RED);
    put(s, 4, 5, TroopType.KNIGHT, Team.RED);
    put(s, 4, 3, TroopType.KNIGHT, Team.BLUE);
    s.tile(5, 4).setFlag(Team.RED);
    s.tile(3, 4).setFlag(Team.BLUE);

    Set<Position> targets = Rules.moveTargets(s, new Position(4, 4));
    assertEquals(Set.of(new Position(4, 3), new Position(3, 4)), targets);
  }

  @Test
  public void wizardSwapTargets() {
    BoardState s = emptyBoard(Terrain.CLASSIC);
    Position wizard = new Position(0, 0);
    put(s, 0, 0, TroopType.WIZARD, Team.BLUE);
    put(s, 1, 0, TroopType.KNIGHT, Team.RED);   // adjacent enemy: capture, not swap
    put(s, 0, 3, TroopType.KNIGHT, Team.BLUE);  // friendly in range
    put(s, 3, 3, TroopType.PRINCE, Team.RED);   // enemy at exactly 6
    put(s, 4, 3, TroopType.PRINCE, Team.RED);   // distance 7
    s.tile(0, 5).setType(TileType.RIVER);
    put(s, 0, 5, TroopType.FAIRY, Team.RED);    // on a river

    Set<Position> moves = Rules.moveTargets(s, wizard);
    assertEquals(Set.of(new Position(0, 3), new Position(3, 3)), Rules.swapTargets(s, wizard, moves));
  }

  @Test
  public void startingLayoutIsSymmetric() {
    for (Terrain terrain : Terrain.values()) {
      BoardState s = Layouts.newGame(terrain);
      int blue = 0;
      int red = 0;
      for (int x = 0; x < BoardState.WIDTH; x++) {
        for (int y = 0; y < BoardState.HEIGHT; y++) {
          Troop t = s.tile(x, y).troop();
          if (t != null) {
            if (t.team() == Team.BLUE) blue++; else red++;
          }
        }
      }
      assertEquals(16, blue);
      assertEquals(16, red);
      assertEquals(Team.RED, s.turn());
    }
  }
}
