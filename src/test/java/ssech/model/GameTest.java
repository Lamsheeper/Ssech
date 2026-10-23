package ssech.model;

import static ssech.Assert.assertEquals;
import static ssech.Assert.assertFalse;
import static ssech.Assert.assertTrue;
import static ssech.model.RulesTest.put;

import java.util.Optional;
import ssech.Test;
import ssech.persistence.SaveCodec;

public class GameTest {

  private static Position at(int x, int y) {
    return new Position(x, y);
  }

  private static void move(Game g, int fx, int fy, int tx, int ty) {
    g.click(at(fx, fy));
    g.click(at(tx, ty));
  }

  @Test
  public void onlyTheTeamToMoveCanSelect() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 0, 0, TroopType.KNIGHT, Team.BLUE);
    put(s, 9, 11, TroopType.KNIGHT, Team.RED);
    Game g = new Game(s);

    g.click(at(0, 0));
    assertEquals(Optional.empty(), g.selected());
    g.click(at(9, 11));
    assertEquals(Optional.of(at(9, 11)), g.selected());
  }

  @Test
  public void deselectingKeepsTheTurn() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 4, 4, TroopType.KNIGHT, Team.RED);
    Game g = new Game(s);
    g.click(at(4, 4));
    g.click(at(4, 4));
    assertEquals(Optional.empty(), g.selected());
    assertEquals(Team.RED, s.turn());
  }

  @Test
  public void captureRemovesEnemyAndEndsTurn() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 4, 6, TroopType.KNIGHT, Team.RED);
    put(s, 4, 5, TroopType.KNIGHT, Team.BLUE);
    Game g = new Game(s);
    int[] turnsEnded = {0};
    g.setOnTurnEnd(() -> turnsEnded[0]++);

    move(g, 4, 6, 4, 5);
    assertEquals(new Troop(TroopType.KNIGHT, Team.RED), s.tile(4, 5).troop());
    assertFalse(s.tile(4, 6).isOccupied(), "origin still occupied");
    assertEquals(Team.BLUE, s.turn());
    assertEquals(1, turnsEnded[0]);
  }

  @Test
  public void banditGetsOneTileFollowUpAfterCapture() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 4, 6, TroopType.BANDIT, Team.RED);
    put(s, 4, 5, TroopType.KNIGHT, Team.BLUE);
    Game g = new Game(s);

    move(g, 4, 6, 4, 5);
    assertEquals(Team.RED, s.turn());
    assertEquals(Optional.of(at(4, 5)), g.selected());
    assertFalse(g.moveTargets().contains(at(4, 3)), "follow-up range should be 1");

    g.click(at(4, 4));
    assertEquals(Team.BLUE, s.turn());
    assertTrue(s.tile(4, 4).isOccupied(), "bandit did not move");
  }

  @Test
  public void deselectingBanditFollowUpPassesTurn() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 4, 6, TroopType.BANDIT, Team.RED);
    put(s, 4, 5, TroopType.KNIGHT, Team.BLUE);
    Game g = new Game(s);

    move(g, 4, 6, 4, 5);
    g.click(at(4, 5));
    assertEquals(Team.BLUE, s.turn());
  }

  @Test
  public void banditPlainMoveEndsTurn() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 4, 6, TroopType.BANDIT, Team.RED);
    Game g = new Game(s);
    move(g, 4, 6, 4, 4);
    assertEquals(Team.BLUE, s.turn());
  }

  @Test
  public void wizardSwapsPlaces() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 2, 11, TroopType.WIZARD, Team.RED);
    put(s, 2, 7, TroopType.KNIGHT, Team.BLUE);
    Game g = new Game(s);

    move(g, 2, 11, 2, 7);
    assertEquals(TroopType.WIZARD, s.tile(2, 7).troop().type());
    assertEquals(new Troop(TroopType.KNIGHT, Team.BLUE), s.tile(2, 11).troop());
    assertEquals(Team.BLUE, s.turn());
  }

  @Test
  public void twoFlagCapturesWin() {
    BoardState s = new BoardState(Terrain.CLASSIC);
    put(s, 0, 1, TroopType.KNIGHT, Team.RED);
    put(s, 9, 11, TroopType.KNIGHT, Team.BLUE);
    s.tile(0, 0).setFlag(Team.BLUE);
    s.tile(1, 0).setFlag(Team.BLUE);
    Game g = new Game(s);

    move(g, 0, 1, 0, 0);
    assertEquals(1, s.score(Team.RED));
    assertEquals(null, s.tile(0, 0).flag());
    assertFalse(g.isOver(), "over after one flag");

    move(g, 9, 11, 9, 10);
    move(g, 0, 0, 1, 0);
    assertTrue(g.isOver(), "not over after two flags");
    assertEquals(Optional.of(Team.RED), g.winner());

    g.click(at(9, 10));
    assertEquals(Optional.empty(), g.selected());
  }

  @Test
  public void forfeitWithEqualScoresIsATie() {
    Game g = new Game(Layouts.newGame(Terrain.CLASSIC));
    g.forfeit();
    assertTrue(g.isOver(), "not over");
    assertEquals(Optional.empty(), g.winner());
  }

  @Test
  public void iceDrownsTroopsAfterFiveTurns() {
    BoardState s = new BoardState(Terrain.TUNDRA);
    s.tile(4, 5).setType(TileType.ICE);
    put(s, 4, 5, TroopType.KNIGHT, Team.RED);
    put(s, 0, 0, TroopType.KNIGHT, Team.BLUE);
    put(s, 9, 11, TroopType.KNIGHT, Team.RED);
    Game g = new Game(s);

    int[][] redShuffle = {{9, 11, 9, 10}, {9, 10, 9, 11}};
    int[][] blueShuffle = {{0, 0, 0, 1}, {0, 1, 0, 0}};
    for (int turn = 0; turn < 4; turn++) {
      int[] m = (turn % 2 == 0 ? redShuffle : blueShuffle)[(turn / 2) % 2];
      move(g, m[0], m[1], m[2], m[3]);
    }
    assertEquals(1, s.tile(4, 5).iceDurability());
    assertTrue(s.tile(4, 5).isOccupied(), "drowned too early");

    move(g, 9, 11, 9, 10);
    assertFalse(s.tile(4, 5).isOccupied(), "did not drown");
    assertEquals(TileType.RIVER, s.tile(4, 5).type());
  }

  @Test
  public void fairiesDoNotCrackIce() {
    BoardState s = new BoardState(Terrain.TUNDRA);
    s.tile(4, 5).setType(TileType.ICE);
    put(s, 4, 5, TroopType.FAIRY, Team.RED);
    put(s, 0, 0, TroopType.KNIGHT, Team.BLUE);
    Game g = new Game(s);
    move(g, 4, 5, 4, 4);
    move(g, 0, 0, 0, 1);
    move(g, 4, 4, 4, 5);
    move(g, 0, 1, 0, 0);
    assertEquals(Tile.FULL_ICE_DURABILITY, s.tile(4, 5).iceDurability());
  }

  @Test
  public void crackedBridgeCollapsesBehindNonFairies() {
    BoardState s = new BoardState(Terrain.VOLCANIC);
    s.tile(4, 6).setType(TileType.CRACKED_BRIDGE);
    s.tile(6, 6).setType(TileType.CRACKED_BRIDGE);
    put(s, 4, 6, TroopType.KNIGHT, Team.RED);
    put(s, 6, 6, TroopType.FAIRY, Team.BLUE);
    Game g = new Game(s);

    move(g, 4, 6, 4, 7);
    assertEquals(TileType.RIVER, s.tile(4, 6).type());
    move(g, 6, 6, 6, 5);
    assertEquals(TileType.CRACKED_BRIDGE, s.tile(6, 6).type());
  }

  @Test
  public void restartRestoresStartingLayout() {
    Game g = new Game(Layouts.newGame(Terrain.VOLCANIC));
    move(g, 1, 10, 1, 9);
    g.forfeit();
    g.restart();
    assertFalse(g.isOver(), "still over after restart");
    assertEquals(SaveCodec.encode(Layouts.newGame(Terrain.VOLCANIC)), SaveCodec.encode(g.state()));
  }
}
