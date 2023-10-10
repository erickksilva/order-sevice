package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataR2dbcTest
@Import(DataConfig.class)
@Testcontainers
class OrderRepositoryR2dbcTests {


    @Container

    static PostgreSQLContainer<?> postgresql =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.4"));
    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource

    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
    }

    private static String r2dbcUrl() {

        return String.format("r2dbc:postgresql://%s:%s/%s",
//                postgresql.getContainerIpAddress(),
                postgresql.getHost(),
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                postgresql.getDatabaseName());
    }

    @Test
    void createRejectedOrder() {
        var rejectedOrder = OrderService.buildRejectedOrder("1234567890", 3);
        StepVerifier
                .create(orderRepository.save(rejectedOrder))

                .expectNextMatches(

                        order -> order.status().equals(OrderStatus.REJECTED))
                .verifyComplete();

    }

/**
 * 1 - Identifica uma classe de teste que se concentra em componentes R2DBC
 * 2 - Importa a configuração R2DBC necessária para permitir a auditoria
 * 3 - Ativa a inicialização e limpeza automática de contêineres de teste
 * 4 - Identifica um contêiner PostgreSQL para teste
 * 5 - Sobrescreve a configuração R2DBC e Flyway para apontar para a instância de teste do PostgreSQL
 * 6 - Constrói uma String de conexão R2DBC, porque Testcontainers não fornece uma pronta para uso, pois faz para JDBC
 * 7 - Inicializa um objeto StepVerifier com o objeto retornado por OrderRepository
 * 8 - Afirma que o pedido retornado tem o status correto
 * 9 - Verifica se o fluxo reativo foi concluído com sucesso
 */
}
