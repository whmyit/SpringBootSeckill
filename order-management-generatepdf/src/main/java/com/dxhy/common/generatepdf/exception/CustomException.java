package com.dxhy.common.generatepdf.exception;
/**
 * 通用异常
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:42
 */
public class CustomException extends Exception
{
  private static final long serialVersionUID = 1L;
    private final int code;

  public CustomException(int code, String message)
  {
    super(message);
    this.code = code;
  }

  public CustomException(int code, String message, Exception e) {
    super(message, e);
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }
}
