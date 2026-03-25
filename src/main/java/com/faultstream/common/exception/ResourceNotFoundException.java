/* =======================================================================
 * Mesela ID si 5 olan bir sensörü arıyorum ve bulamadım. 
 * İşte o zaman hata ekranı basmak yerine bunu kullanacağım.
 * ======================================================================= */
package com.faultstream.common.exception;

// Her seferinde try-catch döngüsü yazmamak için RuntimeException kullanıyorum
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}