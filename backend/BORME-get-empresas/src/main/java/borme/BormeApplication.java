package borme;

import borme.application.runner.BormeTestRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BormeApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BormeApplication.class, args);

        // Ejecutar prueba automáticamente al iniciar
        BormeTestRunner testRunner = context.getBean(BormeTestRunner.class);
        testRunner.runTests();
    }
}