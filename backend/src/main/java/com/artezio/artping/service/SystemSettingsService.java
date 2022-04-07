package com.artezio.artping.service;

import static com.artezio.artping.entity.setting.SystemSettingsEnum.DEFAULT_PAGE_SIZE;
import static com.artezio.artping.entity.setting.SystemSettingsEnum.RECOMMENDED_DAILY_CHECKS;

import com.artezio.artping.data.repository.SystemSettingsRepository;
import com.artezio.artping.dto.PublicSystemSettingsDto;
import com.artezio.artping.dto.SystemSettingsDto;
import com.artezio.artping.entity.setting.SystemSetting;
import com.artezio.artping.entity.setting.SystemSettingsEnum;
import com.artezio.artping.service.mapper.SystemSettingsMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с настройками системы
 */
@Service
@RequiredArgsConstructor
public class SystemSettingsService {
    private final SystemSettingsRepository systemSettingsRepository;
    private final SystemSettingsMapping systemSettingsMapping;

    @Value("${captcha.disable:false}")
    private Boolean isCaptchaDisabled;

    /**
     * Получение списка текущих настроек системы
     *
     * @return список системных настроек
     */
    public SystemSettingsDto getSystemSettings() {
        return systemSettingsMapping.mapToSystemSettingsDto(systemSettingsRepository.findAll());
    }

    /**
     * Информация о текущем статусе капчи
     *
     * @return статус капчи на странице с проверками
     */
    public Boolean getCaptchaSettings() {
        return isCaptchaDisabled;
    }

    /**
     * Получение определенной настройки системы по ключу
     *
     * @param settingKey ключ необходимой настройки системы
     * @return настройка системы с её значением
     */
    public SystemSetting getSystemSetting(SystemSettingsEnum settingKey) {
        SystemSetting setting = new SystemSetting();
        systemSettingsRepository.findById(settingKey).ifPresent(systemSetting -> {
            setting.setSettingKey(systemSetting.getSettingKey());
            setting.setSettingValue(systemSetting.getSettingValue());
        });
        return setting;
    }

    /**
     * Изменение системных настроек
     *
     * @param systemSettingsDto новые данные системных настроек
     */
    public void updateSystemSettings(SystemSettingsDto systemSettingsDto) {
        systemSettingsRepository.saveAll(systemSettingsMapping.mapToSystemSettingList(systemSettingsDto));
    }

    /**
     * Получение настройки, связанной с рекомендованным количеством проверок в день
     *
     * @return значение рекомендованных проверок в день
     */
    public PublicSystemSettingsDto getRecommendedDailyChecks() {
        PublicSystemSettingsDto dto = new PublicSystemSettingsDto();
        dto.setRecommendedDailyChecks(getSystemSetting(RECOMMENDED_DAILY_CHECKS).getIntValue());
        return dto;
    }

    /**
     * Получение значения по умолчанию для количества элементов отображаемых на странице.
     *
     * @return значение по умолчанию для количества элементов
     */
    public Integer getDefaultPageSize() {
        return getSystemSetting(DEFAULT_PAGE_SIZE).getIntValue();
    }
}
