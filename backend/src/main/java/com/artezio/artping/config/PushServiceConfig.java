package com.artezio.artping.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.io.Resources;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Свойства push-service
 */
@ConfigurationProperties("push-service")
@Getter
@Setter
public class PushServiceConfig {
    /**
     *  Приватный ключ сервисного аккаунта
     */
    private String serviceAccountPK;

    @PostConstruct
    public void init() throws IOException {
        if(hasBeenInitializedDefault()) return;
        /**
         * Create service account , download json
         */
        URL resource = Resources.getResource(serviceAccountPK);
        InputStream serviceAccount = resource.openStream();

        /*FirebaseApp app = */
        FirebaseApp.initializeApp(FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                //.setDatabaseUrl("https://artping-test.firebaseio.com")
                .build());
    }

    private boolean hasBeenInitializedDefault(){
        long count = FirebaseApp.getApps().stream()
                .filter(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)).count();
        return count>0;
    }

    public PushServiceConfig() {
    }
}
