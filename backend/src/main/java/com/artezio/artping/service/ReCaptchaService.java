package com.artezio.artping.service;

import com.artezio.artping.config.recaptcha.ReCaptchaProperties;
import com.artezio.artping.dto.capcha.GoogleResponseDto;

import java.net.URI;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Сервис для работы с капчей (на данный момент не используется)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReCaptchaService {

    private final ReCaptchaProperties properties;
    private final RestTemplate restTemplate;

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    private static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    /**
     * Проверка статуса прохождения капчи
     *
     * @param response ответ клиента
     * @param ip       адрес
     * @return статус прохождения
     */
    public boolean isResponseSuccess(String response, String ip) {
        if (response.equals("disabled")) {
            return Boolean.TRUE;
        }
        if (!responseSanityCheck(response)) {
            log.error("Response contains invalid characters");
            return Boolean.FALSE;
        }

        URI verifyUri = URI.create(String.format(RECAPTCHA_URL_TEMPLATE, properties.getSecretKey(), response, ip));

        GoogleResponseDto googleResponse = restTemplate.getForObject(verifyUri, GoogleResponseDto.class);

        if (googleResponse == null || !googleResponse.isSuccess()) {
            log.error("reCaptcha was not successfully validated");
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private static boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

}
