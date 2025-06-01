package model;

public enum DaysOfWeek {
  MONDAY("M"),
  TUESDAY("T"),
  WEDNESDAY("W"),
  THURSDAY("R"),
  FRIDAY("F"),
  SATURDAY("S"),
  SUNDAY("U");

  private final String daySymbol;

  DaysOfWeek(String symbol) {
    this.daySymbol = symbol;
  }

  public String symbol() {
    return this.daySymbol;
  }

  public static DaysOfWeek fromSymbol(String symbol) {
    for (DaysOfWeek weekday : DaysOfWeek.values()) {
      if (weekday.daySymbol.equals(symbol)) {
        return weekday;
      }
    }
    return null;
  }
}