package com.artezio.artping.entity.setting;

/**
 * Названия настроек
 */
public enum SystemSettingsEnum {
    TEST_SUCCESSFUL_MINUTES, // время на успешное прохождение проверки
    TEST_NO_RESPONSE_MINUTES, // время, после которого проверка считается просроченной
    RECOMMENDED_DAILY_CHECKS,  // рекомендованное количество проверок в день
    JWT_TOKEN_VALIDITY, // время жизни новых JWT токенов в мс
    DEFAULT_PAGE_SIZE // размер страницы по-умолчанию
}
