package de.rathsolutions.config.bruteforce;

import org.springframework.security.core.AuthenticationException;

public class BruteforceAccessDeniedException extends AuthenticationException {

    public BruteforceAccessDeniedException(String msg) {
        super(msg);
    }

}
