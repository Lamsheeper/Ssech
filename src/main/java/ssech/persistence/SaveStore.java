package ssech.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import ssech.model.BoardState;

/** Six save slots stored as text files in {@code ~/.ssech/saves} (or {@code $SSECH_SAVE_DIR}). */
public final class SaveStore {
  public static final int SLOT_COUNT = 6;

  private final Path dir;

  public SaveStore(Path dir) {
    this.dir = dir;
  }

  public static SaveStore defaultStore() {
    String override = System.getenv("SSECH_SAVE_DIR");
    Path dir = override != null && !override.isBlank()
        ? Path.of(override)
        : Path.of(System.getProperty("user.home"), ".ssech", "saves");
    return new SaveStore(dir);
  }

  public Path directory() {
    return dir;
  }

  /** @return the saved game in {@code slot}, or empty if the slot is unused */
  public Optional<BoardState> load(int slot) throws IOException {
    Path file = file(slot);
    if (!Files.exists(file)) {
      return Optional.empty();
    }
    try {
      return SaveCodec.decode(Files.readString(file, StandardCharsets.UTF_8));
    } catch (IllegalArgumentException e) {
      throw new IOException("Save slot " + slot + " is corrupted: " + e.getMessage(), e);
    }
  }

  public void save(int slot, BoardState state) throws IOException {
    write(slot, SaveCodec.encode(state));
  }

  public void clear(int slot) throws IOException {
    write(slot, SaveCodec.encodeEmpty());
  }

  private void write(int slot, String contents) throws IOException {
    Files.createDirectories(dir);
    Files.writeString(file(slot), contents, StandardCharsets.UTF_8);
  }

  private Path file(int slot) {
    if (slot < 1 || slot > SLOT_COUNT) {
      throw new IllegalArgumentException("Save slot must be 1-" + SLOT_COUNT + ": " + slot);
    }
    return dir.resolve("save" + slot + ".txt");
  }
}
