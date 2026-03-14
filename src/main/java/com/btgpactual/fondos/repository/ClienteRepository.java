package com.btgpactual.fondos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.btgpactual.fondos.model.Cliente;

@Repository
public interface ClienteRepository extends MongoRepository<Cliente, String> {
}
