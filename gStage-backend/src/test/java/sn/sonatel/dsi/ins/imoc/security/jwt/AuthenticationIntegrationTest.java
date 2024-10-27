package sn.sonatel.dsi.ins.imoc.security.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import sn.sonatel.dsi.ins.imoc.config.SecurityConfiguration;
import sn.sonatel.dsi.ins.imoc.config.SecurityJwtConfiguration;
import sn.sonatel.dsi.ins.imoc.config.WebConfigurer;
import sn.sonatel.dsi.ins.imoc.management.SecurityMetersService;
import tech.jhipster.config.JHipsterProperties;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        JHipsterProperties.class,
        WebConfigurer.class,
        SecurityConfiguration.class,
        SecurityJwtConfiguration.class,
        SecurityMetersService.class,
        JwtAuthenticationTestUtils.class,
    }
)
public @interface AuthenticationIntegrationTest {
}
