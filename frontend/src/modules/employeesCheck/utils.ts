import {endOfWeek, startOfWeek} from "date-fns";

export const weekStartsOn = 1;

export const getStartDate = (date: Date): Date => {
    const current = new Date();
    if (date.getTime() < current.getTime())
        return current
    return date;
}

export function getDefaultPeriod() {
    const currentDate = new Date();
    return {
        type: 'week',
        from: startOfWeek(currentDate,{weekStartsOn:1}),
        to: endOfWeek(currentDate, {weekStartsOn})
    }
}
