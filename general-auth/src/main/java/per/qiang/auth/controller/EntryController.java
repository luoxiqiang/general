package per.qiang.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import per.qiang.auth.exception.VerificationCodeException;
import per.qiang.auth.service.VerificationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class EntryController {

    private final VerificationService verificationService;

    @GetMapping("/getVerify")
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException, VerificationCodeException {
        verificationService.create(request, response);
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> user(Principal principal) {
        return ResponseEntity.ok(principal);
    }
}
