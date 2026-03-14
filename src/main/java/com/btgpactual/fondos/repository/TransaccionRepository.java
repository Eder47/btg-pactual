package com.btgpactual.fondos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.btgpactual.fondos.model.Transaccion;

import java.util.List;

@Repository
public interface TransaccionRepository extends MongoRepository<Transaccion, String> {
 List<Transaccion> findByIdClienteOrderByFechaDesc(String idCliente);
}
