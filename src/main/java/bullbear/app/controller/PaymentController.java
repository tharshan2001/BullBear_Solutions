package bullbear.app.controller;

import bullbear.app.entity.user.User;
import bullbear.app.service.PaymentService;
import bullbear.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam String email,
                                           @RequestParam double amount,  // changed from BigDecimal
                                           @RequestParam String network) {
        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> invoice = paymentService.createInvoice(user, amount, network); // update PaymentService to accept double

            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
