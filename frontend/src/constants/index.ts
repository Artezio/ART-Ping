import { format, add } from 'date-fns';

export const API_URL = process.env.REACT_APP_PUBLIC || "";
export const API_PREFIX = API_URL + (process.env.REACT_APP_URL_PREFIX || "/api");

export const NOTIFICATION_TRANSPORT = process.env.REACT_APP_NOTIFICATION_TRANSPORT || "firebase"; // string ['firebase'|'internalhttp']

enum WARN_LEVEL_ENUM {
    'ALL',
    'CRITICAL',
    'DEBUG',
};

export const WARN_LEVEL: WARN_LEVEL_ENUM = 0

const currentDate: Date = new Date(2021, 1, 14);//parse('​​​​2021-02-15', "yyyy-MM-dd", new Date());

export const DEFAULT_EMPLOYEE_TESTS_FILTER_STATE = {
    "startPeriod": format(currentDate, "yyyy-MM-dd"),
    "endPeriod": format(add(currentDate, { weeks: 1 }), "yyyy-MM-dd"),
    "limit": 20,
    "name": "",
    "officeId": "",
    "offset": 0,
    "projectId": ""
}

export const DEFAULT_CALENDAR_PROPS = {
    name: '',
    weekendDays: [0, 6],
    workHoursFrom: `09:00`,
    workHoursTo: `18:00`,
    startWeekDay: 1
};

export const DEFAULT_USER_PROPS = {
    firstName: '',
    lastName: '',
    email: '',
    login: '',
    baseOffice: '',
    weekendDays: [0, 6],
    workHoursFrom: `09:00`,
    workHoursTo: `18:00`,
    startWeekDay: 1,
    managedOffices: [],
    managedProjects: [],
    offices: [],
    projects: [],
}
