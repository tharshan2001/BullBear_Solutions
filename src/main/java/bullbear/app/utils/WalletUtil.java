package bullbear.app.utils;

import bullbear.app.entity.user.User;
import bullbear.app.service.WalletService;
import org.springframework.stereotype.Component;

@Component
public class WalletUtil {

    private final WalletService walletService;

    public WalletUtil(WalletService walletService) {
        this.walletService = walletService;
    }

    // Create default wallets for a new user
    public void createDefaultWallets(User user) {
        walletService.createWallet(user, "CW");
        walletService.createWallet(user, "USDT");
    }
}