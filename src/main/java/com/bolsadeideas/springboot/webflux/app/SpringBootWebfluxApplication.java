package com.bolsadeideas.springboot.webflux.app;

import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductoService service;
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();

		Categoria electronico = new Categoria("Electrónico");
		Categoria deporte = new Categoria("Deporte");
		Categoria tecnologia = new Categoria("Tecnología");
		Categoria muebles = new Categoria("Muebles");

		Flux.just(electronico, deporte, tecnologia, muebles)
				.flatMap(service::saveCategoria)
				.doOnNext(c -> {
					log.info("Categoría creada: " + c.getNombre() + ", ID: " + c.getId());
				}).thenMany(
						Flux.just(new Producto("TV Panasonic Pantalla LCD", 456.99, electronico),
										new Producto("Sony Cámara HD Digital", 177.99, tecnologia),
										new Producto("Apple iPad", 46.99, tecnologia),
										new Producto("Sony Notebook", 846.99, tecnologia),
										new Producto("Hewlett Packard Multifuncional", 200.99, tecnologia),
										new Producto("Bianchi Bicicleta", 70.99, deporte),
										new Producto("HP Notebook Omen 17", 2500.99, tecnologia),
										new Producto("Mica Cómoda 5 Cajones", 150.99, muebles),
										new Producto("TV Sonic Bravia OLED 4K Ultra HD", 2255.99, electronico)
								)
								.flatMap(producto -> {
									producto.setCreateAt(new Date());
									return service.save(producto);
								})
				)
				.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));
	}
}
