import { Locale } from 'date-fns';
import { enUS } from 'date-fns/locale';
import { ru } from 'date-fns/locale';

type DaysInWeekProps = {
  locale?: Locale;
};

export const daysInWeek = ({ locale = ru }: DaysInWeekProps) => [
  { day: 1, label: locale.localize?.day(1) },
  { day: 2, label: locale.localize?.day(2) },
  { day: 3, label: locale.localize?.day(3) },
  { day: 4, label: locale.localize?.day(4) },
  { day: 5, label: locale.localize?.day(5) },
  { day: 6, label: locale.localize?.day(6) },
  { day: 0, label: locale.localize?.day(0) },
];

export const MONTHS = ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];
export const WEEK_DAYS = ["Воскресенье","Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота",];
export const WEEK_DAYS_SHORT = ["Вс","Пн", "Вт", "Ср", "Чт", "Пт", "Сб",];
export const WEEKEND_DAYS: any[] = [];
