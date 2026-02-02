package bullbear.app.controller;

import bullbear.app.entity.user.User;
import bullbear.app.security.CurrentUser;
import bullbear.app.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<?> getMyWallets(@CurrentUser User user) {
        return ResponseEntity.ok(walletService.getUserWallets(user));
    }
}
