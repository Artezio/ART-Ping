import * as yup from "yup";
import { setLocale } from 'yup';
import i18next from "i18next";
setLocale({
    mixed: {
        required: () => i18next.t('Fill field'),
    },
    string: {
        email: () => i18next.t('Email format not valid'),
    }
});
export const ValidationSchema = yup.object().shape({
    // general info
    id: yup.string(),
    firstName: yup.string().required(),
    lastName: yup.string().required(),
    email: yup.string().email().required(),
    baseOffice: yup.string().required(),
    login: yup.string().required(),
    password: yup.string().nullable(),

    // schedule
    calendarId: yup.string().nullable(),
    isCustomCalendar: yup.bool().nullable(),
    calendarName: yup.string().nullable(),
    workHoursFrom: yup.string().nullable(),
    workHoursTo: yup.string().nullable(),
    startWeekDay: yup.number().nullable(),
    weekendDays: yup.array().nullable(),
    events: yup.array().nullable(),

    //roles
    roles: yup.array().nullable(),
    projects: yup.array().nullable(),
    office: yup.array().nullable(),
    offices: yup.array().nullable(),
    managedProjects: yup.array().nullable(),
});

/*
enum ROLES {
    USER = "USER",
    PM = "PM",
    HR = "HR",
    ADMIN = "ADMIN",
    DIRECTOR = "DIRECTOR",
    ROLE_ANONYMOUS = "ROLE_ANONYMOUS"

}

interface IEvent {
    title: string;
    type: string;
    date: Date;
}

interface IUser {
    // general info
    id:uuid,
    firstName: string,
    lastName: string,
    email: string,
    baseOffice: uuid,
    login: string,
    password: string,

    // schedule
    calendarId: uuid,
    isCustomCalendar:bool,
    calendarName: string,
    workHoursFrom: string,
    workHoursTo: string,
    startWeekDay: number,
    weekendDays: number[], // 0-6
    events: IEvent[],

    //roles
    roles: ROLES[],

    //as employee
    projects: uuid[],

    //as director
    office: uuid,

    //as hr
    offices:uuid[],

    // as pm
    managedProjects: uuid[],
}
*/
