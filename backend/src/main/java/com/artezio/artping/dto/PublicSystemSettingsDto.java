package com.artezio.artping.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ДТО с информацией о рекомендованном количестве проверок в день из системных настроек
 */
@Getter
@Setter
public class PublicSystemSettingsDto {

    /**
     * Рекомендованное количество проверок в день
     */
    private int recommendedDailyChecks;
    
}
