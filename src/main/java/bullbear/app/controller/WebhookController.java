package bullbear.app.controller;

import bullbear.app.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class WebhookController {

    private final PaymentService paymentService;

    public WebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            String status = (String) payload.get("payment_status");
            if ("finished".equalsIgnoreCase(status)) {
                // Convert amount to double
                double amount = Double.parseDouble(payload.get("pay_amount").toString());
                String network = (String) payload.get("network");

                // Credit owner wallet
                paymentService.creditOwnerWallet(amount, network);
            }
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
