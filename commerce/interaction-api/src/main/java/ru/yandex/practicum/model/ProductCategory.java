package ru.yandex.practicum.model;

public enum ProductCategory {
  
  LIGHTING("LIGHTING"),
  
  CONTROL("CONTROL"),
  
  SENSORS("SENSORS");

  private String value;

  ProductCategory(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static ProductCategory fromValue(String value) {
    for (ProductCategory b : ProductCategory.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}