package borme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BormeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BormeApplication.class, args);

        // Líneas no aptas para producción:
        // ConfigurableApplicationContext context = SpringApplication.run(BormeApplication.class, args);
        // BormeTestRunner testRunner = context.getBean(BormeTestRunner.class);
        // testRunner.runTests();
        // BormeIntegrationTest bormeIntegrationTest = context.getBean(BormeIntegrationTest.class);
        // bormeIntegrationTest.runTests();
    }
}