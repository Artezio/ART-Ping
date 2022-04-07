package com.artezio.artping;

import com.artezio.artping.config.PushServiceConfig;
import com.artezio.artping.entity.ArtPingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({ArtPingProperties.class, PushServiceConfig.class})
@EnableScheduling
@EntityScan("com.artezio.artping.entity")
@EnableJpaRepositories("com.artezio.artping.data.repository")
@PropertySource("classpath:application.properties")
@PropertySource("classpath:git.properties")
public class ArtpingApplication {

  public static void main(String[] args) {
    SpringApplication.run(ArtpingApplication.class, args);
  }

}

