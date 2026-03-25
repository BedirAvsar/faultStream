/* =======================================================================
 * Sisteme giriş yetkisi olmayan kişileri yakalamak için kullanacağım.
 * ======================================================================= */
package com.faultstream.common.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}