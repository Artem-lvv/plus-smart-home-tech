package ru.yandex.practicum.model.productEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductCategory {
  LIGHTING("LIGHTING"),
  CONTROL("CONTROL"),
  SENSORS("SENSORS");

  private String value;

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