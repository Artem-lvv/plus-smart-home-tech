package ru.yandex.practicum.model.productEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Статус, перечисляющий состояние остатка как свойства товара
 */

@Getter
@AllArgsConstructor
public enum QuantityState {
  ENDED("ENDED"),
  FEW("FEW"),
  ENOUGH("ENOUGH"),
  MANY("MANY");

  private String value;

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static QuantityState fromValue(String value) {
    for (QuantityState b : QuantityState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}