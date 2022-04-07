package com.artezio.artping.entity.setting;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность "Системная настройка"
 */
@Entity
@Table(name = "system_settings", schema = "art_ping")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SystemSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Идентификатор сущности, оно же название настройки
     */
    @Id
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemSettingsEnum settingKey;

    /**
     * Значение настройки
     */
    @Column
    private String settingValue;

    public int getIntValue() {
        return Integer.parseInt(settingValue);
    }

    public long getLongValue() {
        return Long.parseLong(settingValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SystemSetting that = (SystemSetting) o;
        return settingKey == that.settingKey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingKey);
    }

}
