package com.btgpactual.fondos.config;

import com.btgpactual.fondos.model.Cliente;
import com.btgpactual.fondos.model.Fondo;
import com.btgpactual.fondos.repository.ClienteRepository;
import com.btgpactual.fondos.repository.FondoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import java.math.BigDecimal;
import java.util.ArrayList;

@Configuration
@EnableMongoRepositories(basePackages = "com.btgpactual.fondos.repository")
public class MongoConfig {
    
    @Bean
    public CommandLineRunner loadInitialData(ClienteRepository clienteRepository, 
                                              FondoRepository fondoRepository) {
        return args -> {
            if (fondoRepository.count() == 0) {
                for (Fondo fondo : Fondo.getFondosIniciales()) {
                    fondoRepository.save(fondo);
                }
            }
            
            if (clienteRepository.count() == 0) {
                Cliente cliente = Cliente.builder()
                    .id("1")
                    .nombre("Cliente Prueba")
                    .email("cliente@test.com")
                    .telefono("+573001234567")
                    .tipoNotificacion("EMAIL")
                    .saldo(new BigDecimal("500000"))
                    .fondosSuscritos(new ArrayList<>())
                    .build();
                clienteRepository.save(cliente);
            }
        };
    }
}