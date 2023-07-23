package demo.ddz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application.
 */
@SpringBootApplication
public class Application {

  /**
   * Constructor.
   */
  public Application() {
  }

  /**
   * Main entry.
   *
   * @param args array of {@link String}
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
