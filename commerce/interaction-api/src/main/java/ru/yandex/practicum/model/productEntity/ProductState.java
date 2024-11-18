package ru.yandex.practicum.model.productEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductState {
  ACTIVE("ACTIVE"),
  DEACTIVATE("DEACTIVATE");

  private String value;

    @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static ProductState fromValue(String value) {
    for (ProductState b : ProductState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}