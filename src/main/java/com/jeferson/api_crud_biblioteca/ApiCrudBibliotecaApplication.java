package com.jeferson.api_crud_biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiCrudBibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCrudBibliotecaApplication.class, args);
	}

}
