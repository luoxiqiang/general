package per.qiang.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class VerificationCodeException extends AuthenticationException {

    public VerificationCodeException(String message) {
        super(message);
    }
}
