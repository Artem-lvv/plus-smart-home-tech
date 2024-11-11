package ru.yandex.practicum.model.entity.product;

/**
 * Статус, перечисляющий состояние остатка как свойства товара
 */

public enum QuantityState {
  
  ENDED("ENDED"),
  
  FEW("FEW"),
  
  ENOUGH("ENOUGH"),
  
  MANY("MANY");

  private String value;

  QuantityState(String value) {
    this.value = value;
  }

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