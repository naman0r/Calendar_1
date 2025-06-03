package model;

/**
 * Represents the status of an event: either PUBLIC or PRIVATE.
 */
public enum Status {
  PUBLIC,
  PRIVATE;

  /**
   * Parse a user‐provided string into one of the two valid enum constants.
   * @throws IllegalArgumentException if the string is null/empty or not
   *                                  "public" / "private" (case‐insensitive).
   */
  public static Status valueOfStrict(String s) {
    if (s == null || s.trim().isEmpty()) {
      throw new IllegalArgumentException("Status cannot be null or empty");
    }
    switch (s.trim().toLowerCase()) {
      case "public":  return PUBLIC;
      case "private": return PRIVATE;
      default:
        throw new IllegalArgumentException("Unsupported status: " + s);
    }
  }

  @Override
  public String toString() {
    return name().toLowerCase(); // so that PUBLIC→"public", PRIVATE→"private"
  }
}
