package bullbear.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import bullbear.app.entity.wallet.Wallet;
import bullbear.app.entity.wallet.WalletType;
import bullbear.app.entity.user.User;
import bullbear.app.repository.wallet.WalletRepository;
import bullbear.app.repository.wallet.WalletTypeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${nowpayments.api.key}")
    private String apiKey;

    @Value("${owner.wallet.address}")
    private String ownerWalletAddress; // USDT TRC20/BEP20 address

    private final WalletRepository walletRepository;
    private final WalletTypeRepository walletTypeRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentService(WalletRepository walletRepository,
                          WalletTypeRepository walletTypeRepository) {
        this.walletRepository = walletRepository;
        this.walletTypeRepository = walletTypeRepository;
    }

    // ============================
    // Create NowPayments Invoice
    // ============================
    public Map<String, Object> createInvoice(User user, double amount, String network) throws Exception {
        String url = "https://api.nowpayments.io/v1/invoice";

        Map<String, Object> body = new HashMap<>();
        body.put("price_amount", amount);
        body.put("price_currency", "USDT");
        body.put("pay_currency", "USDT");
        body.put("pay_address", ownerWalletAddress);
        body.put("network", network); // TRC20 or BEP20
        body.put("order_id", "user_" + user.getId() + "_" + System.currentTimeMillis());
        body.put("ipn_callback_url", "https://yourapp.com/api/payment/webhook");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode json = objectMapper.readTree(response.getBody());
            Map<String, Object> result = new HashMap<>();
            result.put("invoice_id", json.get("id").asText());
            result.put("payment_address", json.get("pay_address").asText());
            result.put("amount", json.get("price_amount").asDouble()); // use double
            result.put("network", network);
            return result;
        } else {
            throw new RuntimeException("Failed to create invoice: " + response.getBody());
        }
    }

    // ============================
    // Credit owner wallet (USDT)
    // ============================
    public void creditOwnerWallet(double amount, String walletTypeName) {
        WalletType type = walletTypeRepository.findByName(walletTypeName)
                .orElseThrow(() -> new RuntimeException("Wallet type not found: " + walletTypeName));

        // Find owner wallet
        Wallet ownerWallet = walletRepository
                .findByUser_IdAndWalletType_WalletTypeId(1L, type.getWalletTypeId())
                .orElseThrow(() -> new RuntimeException("Owner wallet not found"));

        ownerWallet.setBalance(ownerWallet.getBalance() + amount);
        walletRepository.save(ownerWallet);
    }
}
