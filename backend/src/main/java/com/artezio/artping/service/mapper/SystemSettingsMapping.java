package com.artezio.artping.service.mapper;

import com.artezio.artping.dto.SystemSettingsDto;
import com.artezio.artping.entity.setting.SystemSetting;
import com.artezio.artping.entity.setting.SystemSettingsEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import static com.artezio.artping.entity.setting.SystemSettingsEnum.*;

@Component
public class SystemSettingsMapping {

    public SystemSettingsDto mapToSystemSettingsDto(List<SystemSetting> systemSettings) {
        Map<SystemSettingsEnum, SystemSetting> systemSettingsMap = systemSettings.stream()
                .collect(Collectors.toMap(SystemSetting::getSettingKey, systemSetting -> systemSetting));
        SystemSettingsDto dto = new SystemSettingsDto();
        dto.setRecommendedDailyChecks(systemSettingsMap.get(
                RECOMMENDED_DAILY_CHECKS).getIntValue());
        dto.setTestNoResponseMinutes(systemSettingsMap.get(TEST_NO_RESPONSE_MINUTES).getIntValue());
        dto.setTestSuccessfulMinutes(systemSettingsMap.get(TEST_SUCCESSFUL_MINUTES).getIntValue());
        dto.setJwtTokenValidity(systemSettingsMap.get(JWT_TOKEN_VALIDITY).getLongValue());
        return dto;
    }

    public List<SystemSetting> mapToSystemSettingList(SystemSettingsDto dto) {
        List<SystemSetting> settings = new ArrayList<>();
        settings.add(
                new SystemSetting(RECOMMENDED_DAILY_CHECKS, dto.getRecommendedDailyChecks().toString()));
        settings.add(
                new SystemSetting(TEST_NO_RESPONSE_MINUTES, dto.getTestNoResponseMinutes().toString()));
        settings
                .add(new SystemSetting(TEST_SUCCESSFUL_MINUTES, dto.getTestSuccessfulMinutes().toString()));
        settings.add(new SystemSetting(JWT_TOKEN_VALIDITY, dto.getJwtTokenValidity().toString()));
        return settings;
    }

}
