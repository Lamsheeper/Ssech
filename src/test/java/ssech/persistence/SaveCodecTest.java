package ssech.persistence;

import static ssech.Assert.assertEquals;
import static ssech.Assert.assertThrows;
import static ssech.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import ssech.Test;
import ssech.model.BoardState;
import ssech.model.Layouts;
import ssech.model.Team;
import ssech.model.Terrain;
import ssech.model.Tile;
import ssech.model.TileType;

public class SaveCodecTest {

  @Test
  public void roundTripsEveryTerrain() {
    for (Terrain terrain : Terrain.values()) {
      BoardState original = Layouts.newGame(terrain);
      String encoded = SaveCodec.encode(original);
      assertEquals(encoded, SaveCodec.encode(SaveCodec.decode(encoded).orElseThrow()));
    }
  }

  @Test
  public void preservesTurnScoresAndCrackedIce() {
    BoardState s = Layouts.newGame(Terrain.TUNDRA);
    s.setTurn(Team.BLUE);
    s.setScore(Team.RED, 1);
    s.tile(0, 4).setIceDurability(2);
    s.tile(0, 0).setFlag(null);

    BoardState loaded = SaveCodec.decode(SaveCodec.encode(s)).orElseThrow();
    assertEquals(Team.BLUE, loaded.turn());
    assertEquals(1, loaded.score(Team.RED));
    assertEquals(TileType.ICE, loaded.tile(0, 4).type());
    assertEquals(2, loaded.tile(0, 4).iceDurability());
    assertEquals(null, loaded.tile(0, 0).flag());
  }

  @Test
  public void emptySlotDecodesToNothing() {
    assertEquals(Optional.empty(), SaveCodec.decode("Empty\n"));
    assertEquals(Optional.empty(), SaveCodec.decode(SaveCodec.encodeEmpty()));
  }

  @Test
  public void legacySavesWithoutFlagLineInferFlags() {
    BoardState s = Layouts.newGame(Terrain.CLASSIC);
    s.tile(4, 0).setFlag(null);
    s.tile(4, 0).setTroop(s.tile(4, 10).troop());
    s.tile(4, 10).setTroop(null);

    String legacy = SaveCodec.encode(s).lines()
        .filter(l -> !l.startsWith("Flags"))
        .map(l -> l.contains(" ") ? l + " " : l)
        .reduce("", (a, b) -> a + b + "\n");
    BoardState loaded = SaveCodec.decode(legacy).orElseThrow();

    assertEquals(Team.BLUE, loaded.tile(0, 0).flag());
    assertEquals(null, loaded.tile(4, 0).flag());
    assertEquals(Team.RED, loaded.tile(9, 11).flag());
  }

  @Test
  public void legacySevenPointIceIsCappedAtFullDurability() {
    String encoded = SaveCodec.encode(Layouts.newGame(Terrain.TUNDRA)).replace("Ice5", "Ice7");
    BoardState loaded = SaveCodec.decode(encoded).orElseThrow();
    assertEquals(Tile.FULL_ICE_DURABILITY, loaded.tile(0, 4).iceDurability());
  }

  @Test
  public void rejectsInvalidSaves() {
    String valid = SaveCodec.encode(Layouts.newGame(Terrain.CLASSIC));
    assertThrows(IllegalArgumentException.class, () -> SaveCodec.decode(valid.replaceFirst("Classic", "Orange Map")));
    assertThrows(IllegalArgumentException.class, () -> SaveCodec.decode("Classic\n1\n0\n0\n"));
    assertThrows(IllegalArgumentException.class, () -> SaveCodec.decode(valid.replaceFirst("\n0\n0\n", "\n6\n6\n")));
  }

  @Test
  public void storeSavesLoadsAndClearsSlots() throws IOException {
    Path dir = Files.createTempDirectory("ssech-test").resolve("nested");
    SaveStore store = new SaveStore(dir);
    assertEquals(Optional.empty(), store.load(3));

    BoardState s = Layouts.newGame(Terrain.TUNDRA);
    s.tile(5, 5).setIceDurability(Tile.FULL_ICE_DURABILITY - 1);
    store.save(3, s);
    assertTrue(Files.exists(dir.resolve("save3.txt")), "save file not written");
    assertEquals(SaveCodec.encode(s), SaveCodec.encode(store.load(3).orElseThrow()));

    store.clear(3);
    assertEquals(Optional.empty(), store.load(3));
  }
}
