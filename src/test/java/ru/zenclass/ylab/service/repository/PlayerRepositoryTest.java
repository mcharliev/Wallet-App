package ru.zenclass.ylab.service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.zenclass.ylab.model.entity.Player;
import ru.zenclass.ylab.repository.PlayerRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PlayerRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private PlayerRepository repository;
    private Player player;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("database.url", postgres::getJdbcUrl);
        registry.add("database.password", postgres::getPassword);
        registry.add("database.username", postgres::getUsername);
    }

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setUsername("testUser");
        player.setPassword("testPassword");
        player.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void testAddPlayer() {
        repository.addPlayer(player);

        assertThat(player.getId()).isNotNull();
    }

    @Test
    void testFindPlayerByUsername() {
        repository.addPlayer(player);

        Optional<Player> foundPlayerOpt = repository.findPlayerByUsername(player.getUsername());
        assertThat(foundPlayerOpt).isPresent();

        Player foundPlayer = foundPlayerOpt.get();
        assertThat(foundPlayer.getUsername()).isEqualTo(player.getUsername());
        assertThat(foundPlayer.getPassword()).isEqualTo(player.getPassword());
        assertThat(foundPlayer.getBalance()).isEqualByComparingTo(player.getBalance());
    }

    @Test
    void testFindNonExistentPlayerByUsername() {
        Optional<Player> notFoundPlayerOpt = repository.findPlayerByUsername("nonExistentUsername");
        assertThat(notFoundPlayerOpt).isNotPresent();
    }

    @Test
    void testUpdatePlayer() {
        repository.addPlayer(player);

        player.setBalance(new BigDecimal("400.00"));
        repository.updatePlayer(player);

        Optional<Player> updatedPlayerOpt = repository.findPlayerById(player.getId());
        assertThat(updatedPlayerOpt).isPresent();
        assertThat(updatedPlayerOpt.get().getBalance()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    void testFindPlayerById() {
        repository.addPlayer(player);

        Optional<Player> foundPlayerOpt = repository.findPlayerById(player.getId());
        assertThat(foundPlayerOpt).isPresent();

        Player foundPlayer = foundPlayerOpt.get();
        assertThat(foundPlayer.getUsername()).isEqualTo(player.getUsername());
        assertThat(foundPlayer.getPassword()).isEqualTo(player.getPassword());
        assertThat(foundPlayer.getBalance()).isEqualByComparingTo(player.getBalance());
    }
}
