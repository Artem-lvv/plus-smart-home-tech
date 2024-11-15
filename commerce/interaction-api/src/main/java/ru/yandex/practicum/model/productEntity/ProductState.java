package ru.yandex.practicum.model.productEntity;

public enum ProductState {
  ACTIVE("ACTIVE"),
  DEACTIVATE("DEACTIVATE");

  private String value;

  ProductState(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

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