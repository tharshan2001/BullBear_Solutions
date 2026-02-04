package bullbear.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BlockchainTx {
    private String hash;           // tx_hash
    private String from;
    private String to;
    private BigDecimal amount;         // in USDT
    private int confirmations;
    private String token;          // USDT
}
