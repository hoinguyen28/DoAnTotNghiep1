package vn.ptit.webTranh_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EnableJpaRepositories(basePackages = "vn.ptit.webTranh_backend.dao")
@EntityScan(basePackages = "vn.ptit.webTranh_backend.entity")
@SpringBootApplication
public class WebTranhBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebTranhBackendApplication.class, args);
	}
}
