package com.btgpactual.fondos.repository;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.btgpactual.fondos.model.Fondo;

import java.util.Optional;

@Repository
public interface FondoRepository extends MongoRepository<Fondo, String> {
 Optional<Fondo> findByNombre(String nombre);
}
