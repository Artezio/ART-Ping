import {startOfYear} from "date-fns";

export const currentYear = 1;
export const getStartDate = (date: Date): Date => {
    const current = new Date();
    if (date.getTime() < current.getTime())
        return current
    return date;
}

export function getDefaultYear() {
    const currentDate = new Date();
    return {
        type: 'year',
        from: startOfYear(currentDate),
    }
}