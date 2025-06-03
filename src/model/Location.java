package model;

/**
 * Represents a location for an event: either VIRTUAL or PHYSICAL.
 */
public enum Location {
  VIRTUAL,
  PHYSICAL;

  /**
   * Parse a user‐provided string into one of the two valid enum constants.
   * @throws IllegalArgumentException if the string is null/empty or not
   *                                  "virtual" / "physical" (case‐insensitive).
   */
  public static Location valueOfStrict(String s) {
    if (s == null || s.trim().isEmpty()) {
      throw new IllegalArgumentException("Location cannot be null or empty");
    }
    switch (s.trim().toLowerCase()) {
      case "virtual":  return VIRTUAL;
      case "physical": return PHYSICAL;
      default:
        throw new IllegalArgumentException("Unsupported location: " + s);
    }
  }

  @Override
  public String toString() {
    return name().toLowerCase(); // so that VIRTUAL→"virtual", PHYSICAL→"physical"
  }
}
