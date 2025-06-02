package model;

/**
 * Represents the status of an event (public, private, or custom).
 */
public class Status {
  private final String statusValue;
  
  public static final Status PUBLIC = new Status("public");
  public static final Status PRIVATE = new Status("private");
  
  public Status(String status) {
    this.statusValue = status == null ? "public" : status;
  }
  
  public static Status valueOf(String status) {
    if (status == null || status.trim().isEmpty()) {
      return PUBLIC;
    }
    return new Status(status.trim().toLowerCase());
  }
  
  @Override
  public String toString() {
    return statusValue;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Status)) return false;
    Status other = (Status) obj;
    return statusValue.equals(other.statusValue);
  }
  
  @Override
  public int hashCode() {
    return statusValue.hashCode();
  }
}
