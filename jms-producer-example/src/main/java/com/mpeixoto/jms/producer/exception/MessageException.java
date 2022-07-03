package com.mpeixoto.jms.producer.exception;

/**
 * Custom exception.
 *
 * @author mpeixoto
 */
public class MessageException extends Exception {

  /**
   * Default constructor of the class.
   *
   * @param message Description of the error
   * @param throwable Cause of the error
   */
  public MessageException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
