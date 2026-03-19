package lk.ijse.eca.programservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String productId, Integer available, Integer requested) {
        super(String.format("Insufficient stock for product %s: available=%d, requested=%d", 
                productId, available, requested));
    }
}
