package bullbear.app.config;

import bullbear.app.entity.wallet.WalletType;
import bullbear.app.repository.WalletTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class WalletTypeSeeder {

    private final WalletTypeRepository walletTypeRepository;

    public WalletTypeSeeder(WalletTypeRepository walletTypeRepository) {
        this.walletTypeRepository = walletTypeRepository;
    }

    @PostConstruct
    public void seed() {
        List<String> defaultTypes = List.of("CW", "USDT");

        for (String name : defaultTypes) {
            walletTypeRepository.findByName(name).orElseGet(() ->
                    walletTypeRepository.save(
                            new WalletType(
                                    null,
                                    name,
                                    name + " Wallet",
                                    LocalDateTime.now(),
                                    LocalDateTime.now()
                            )
                    )
            );
        }
    }
}