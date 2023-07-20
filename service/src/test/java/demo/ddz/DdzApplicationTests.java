package demo.ddz;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("DdzApplication")
class DdzApplicationTests {

  @Test
  @DisplayName("contextLoads")
  void contextLoads() {
    assertDoesNotThrow(DdzApplication::new);
    try (MockedStatic<SpringApplication> application = mockStatic(SpringApplication.class)) {
      assertNotNull(application);
      DdzApplication.main(null);
    }
  }
}
