package com.artezio.artping.entity.calendar;

import com.artezio.artping.entity.AbstractEntity;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность "Событие"
 */
@Getter
@Setter
@Entity
@Table(name = "event", schema = "art_ping")
@AllArgsConstructor
@NoArgsConstructor
public class Event extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Дата события
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Тип события
     */
    @Column(name = "type", nullable = false)
    private DayType type;

    /**
     * Описание события
     */
    @Column(name = "description")
    private String description;

    /**
     * Идентификатор календаря, к которому относится событие
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(date, event.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date.hashCode());
    }

}

