package com.bolsadeideas.springboot.webflux.app.dao;

import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String> {

}
