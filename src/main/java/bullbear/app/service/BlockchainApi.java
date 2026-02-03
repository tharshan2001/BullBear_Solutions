package bullbear.app.service;

import bullbear.app.dto.auth.BlockchainTx;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class BlockchainApi {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    private final String TRON_WALLET = "YOUR_TRON_WALLET_ADDRESS";
    private final String BSC_WALLET = "YOUR_BSC_WALLET_ADDRESS";
    private final String BSC_API_KEY = "YourApiKeyToken";

    public BlockchainApi(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public BlockchainTx findDeposit(bullbear.app.entity.transaction.Transaction tx) throws Exception {

        if (tx.getNetwork().equalsIgnoreCase("TRC20")) {
            return checkTron(tx);
        } else if (tx.getNetwork().equalsIgnoreCase("BEP20")) {
            return checkBsc(tx);
        }
        return null;
    }

    private BlockchainTx checkTron(bullbear.app.entity.transaction.Transaction tx) throws Exception {
        String url = "https://api.trongrid.io/v1/accounts/" + TRON_WALLET + "/transactions/trc20?limit=50&order_by=block_timestamp,desc";

        String body = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

        JsonNode root = mapper.readTree(body);
        JsonNode data = root.path("data");

        for (JsonNode node : data) {
            String to = node.path("to").asText();
            String tokenName = node.path("token_info").path("symbol").asText();
            String txHash = node.path("transaction_id").path("hash").asText();
            double amount = node.path("value").asDouble() / 1_000_000; // TRC20 USDT has 6 decimals
            int confirmations = node.path("confirmations").asInt();

            if (to.equalsIgnoreCase(TRON_WALLET)
                    && tokenName.equalsIgnoreCase("USDT")
                    && Math.abs(amount - tx.getAmount()) < 0.0001
                    && confirmations >= 12) {

                return new BlockchainTx(txHash, node.path("from").asText(), to, amount, confirmations, tokenName);
            }
        }
        return null;
    }

    private BlockchainTx checkBsc(bullbear.app.entity.transaction.Transaction tx) throws Exception {
        String url = "https://api.bscscan.com/api?module=account&action=tokentx" +
                "&address=" + BSC_WALLET + "&startblock=0&endblock=99999999&sort=desc&apikey=" + BSC_API_KEY;

        String body = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();
        JsonNode root = mapper.readTree(body);

        if (!root.path("status").asText().equals("1")) return null;
        JsonNode data = root.path("result");

        for (JsonNode node : data) {
            String to = node.path("to").asText();
            String tokenName = node.path("tokenSymbol").asText();
            String txHash = node.path("hash").asText();
            double amount = node.path("value").asDouble() / Math.pow(10, node.path("tokenDecimal").asInt());

            if (to.equalsIgnoreCase(BSC_WALLET)
                    && tokenName.equalsIgnoreCase("USDT")
                    && Math.abs(amount - tx.getAmount()) < 0.0001) {
                // For BEP20, we cannot get confirmations via API free tier
                return new BlockchainTx(txHash, node.path("from").asText(), to, amount, 15, tokenName);
            }
        }
        return null;
    }
}
