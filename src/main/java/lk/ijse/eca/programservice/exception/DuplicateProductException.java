package lk.ijse.eca.programservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateProductException extends RuntimeException {
    
    public DuplicateProductException(String productId) {
        super("Product already exists with ID: " + productId);
    }
}
