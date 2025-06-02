package model;

/**
 * Represents a location for an event, which can be either virtual, physical, or a custom string.
 */
public class Location {
  private final String locationValue;
  
  public static final Location VIRTUAL = new Location("virtual");
  public static final Location PHYSICAL = new Location("physical");
  
  public Location(String location) {
    this.locationValue = location == null ? "" : location;
  }
  
  public static Location valueOf(String location) {
    if (location == null || location.trim().isEmpty()) {
      return new Location("");
    }
    return new Location(location.trim().toLowerCase());
  }
  
  @Override
  public String toString() {
    return locationValue;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Location)) return false;
    Location other = (Location) obj;
    return locationValue.equals(other.locationValue);
  }
  
  @Override
  public int hashCode() {
    return locationValue.hashCode();
  }
}
