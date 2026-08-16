//package com.fitnesscenter.common.exception;
//
//import com.fitnesscenter.common.response.ApiResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//
//import java.util.stream.Collectors;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleNotFound(
//            ResourceNotFoundException exception
//    ) {
//
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(
//                        ApiResponse.error(
//                                exception.getMessage()
//                        )
//                );
//    }
//
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
//            BadRequestException exception
//    ) {
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(
//                        ApiResponse.error(
//                                exception.getMessage()
//                        )
//                );
//    }
//
//
//    @ExceptionHandler(ConflictException.class)
//    public ResponseEntity<ApiResponse<Void>> handleConflict(
//            ConflictException exception
//    ) {
//
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(
//                        ApiResponse.error(
//                                exception.getMessage()
//                        )
//                );
//    }
//
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleGeneralException(
//            Exception exception
//    ) {
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(
//                        ApiResponse.error(
//                                "An unexpected error occurred"
//                        )
//                );
//    }
//
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
//            MethodArgumentNotValidException exception
//    ) {
//
//        String message = exception
//                .getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(error ->
//                        error.getField()
//                                + ": "
//                                + error.getDefaultMessage()
//                )
//                .collect(Collectors.joining(", "));
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(
//                        ApiResponse.error(message)
//                );
//    }
//
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
//            UnauthorizedException exception
//    ) {
//
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(
//                        ApiResponse.error(
//                                exception.getMessage()
//                        )
//                );
//    }
//
//}












package com.fitnesscenter.common.exception;

        import com.fitnesscenter.common.response.ApiResponse;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.MethodArgumentNotValidException;
        import org.springframework.web.bind.annotation.ExceptionHandler;
        import org.springframework.web.bind.annotation.RestControllerAdvice;

        import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException exception
    ) {

        log.warn(
                "Resource not found: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.error(
                                exception.getMessage()
                        )
                );
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            BadRequestException exception
    ) {

        log.warn(
                "Bad request: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.error(
                                exception.getMessage()
                        )
                );
    }


    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(
            ConflictException exception
    ) {

        log.warn(
                "Conflict: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        ApiResponse.error(
                                exception.getMessage()
                        )
                );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField()
                                + ": "
                                + error.getDefaultMessage()
                )
                .collect(Collectors.joining(", "));

        log.warn(
                "Validation failed: {}",
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.error(message)
                );
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            UnauthorizedException exception
    ) {

        log.warn(
                "Unauthorized request: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiResponse.error(
                                exception.getMessage()
                        )
                );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(
            Exception exception
    ) {

        log.error(
                "Unexpected application error",
                exception
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.error(
                                "An unexpected error occurred"
                        )
                );
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleAccessDenied(
            AccessDeniedException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ApiResponse.error(
                                exception.getMessage()
                        )
                );
    }
}