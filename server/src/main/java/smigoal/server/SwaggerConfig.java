package smigoal.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "smigoal API 명세서",
                description = "스팸문자 차단 서비스 스미골 API 명세서",
                version = "v1")
)
@Configuration
public class SwaggerConfig {

}
