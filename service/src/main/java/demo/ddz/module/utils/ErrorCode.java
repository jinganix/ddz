package demo.ddz.module.utils;

import demo.ddz.helper.enumeration.Enumeration;
import demo.ddz.helper.enumeration.IntegerEnumMapper;
import demo.ddz.helper.exception.BusinessErrorCode;

public enum ErrorCode implements BusinessErrorCode, Enumeration<Integer> {
  TABLE_NOT_FOUND(1),
  PHASE_INVALID(2),
  NOT_CURRENT_PLAYER(3),
  INVALID_PLAYED_CARDS(4),
  CARDS_SET_NOT_DOMINATED(5);

  private static final IntegerEnumMapper<ErrorCode> mapper = new IntegerEnumMapper<>(values());

  private final Integer value;

  ErrorCode(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link ErrorCode}
   */
  public static ErrorCode fromValue(Integer value) {
    return mapper.fromValue(value);
  }

  /**
   * Get enumeration value.
   *
   * @return {@link Integer}
   */
  @Override
  public Integer getValue() {
    return value;
  }
}
