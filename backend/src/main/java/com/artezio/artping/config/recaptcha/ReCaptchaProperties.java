package com.artezio.artping.config.recaptcha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Свойства reCAPTCHA
 */
@Component
@ConfigurationProperties("captcha")
@Getter
@Setter
public class ReCaptchaProperties {

    private String siteKey;

    private String secretKey;
}
