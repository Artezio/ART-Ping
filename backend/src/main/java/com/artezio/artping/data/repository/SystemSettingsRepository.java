package com.artezio.artping.data.repository;

import com.artezio.artping.entity.setting.SystemSetting;
import com.artezio.artping.entity.setting.SystemSettingsEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для получения данных о системных настройках
 */
@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSetting, SystemSettingsEnum> {

}
