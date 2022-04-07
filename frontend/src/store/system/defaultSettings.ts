//enum DAY_TYPES{'PAID', 'UNPAID', 'WEEKEND', 'SICKNESS', 'SICK', 'VACATION', 'WORKING_DAY'};
import {CALENDAR_DAY_TYPE} from '../../components/misc/Calendar/'

const DEFAULT_SETTINGS_LIST = [
    {
        id: 0,
        name: "pageSize",
        type: 'number',
        title: "Grid page size",
        isCustomizable: false,
        value: 20
    },
    {
        id: 1,
        name: "recommendedDailyChecks",
        type: 'number',
        title: "Limit per day for autochecks",
        isCustomizable: true,
        value: 16
    },
    {
        id: 2,
        name: "dayTypesList",
        type: 'array',
        title: "Avaliable day types",
        isCustomizable: false,
        value: [
            {
                id: 0,
                name: CALENDAR_DAY_TYPE.WORKING_DAY.toString(),
                value: "Рабочий",
                officeCalendarSetting: true,
                personalCalendarSetting: true
            },
            {
                id: 1,
                name: CALENDAR_DAY_TYPE.WEEKEND.toString(),
                value: "Выходной",
                officeCalendarSetting: true,
                personalCalendarSetting: true
            },
            {
                id: 2,
                name: CALENDAR_DAY_TYPE.HOLIDAY.toString(),
                value: "Праздничный день",
                officeCalendarSetting: true,
                personalCalendarSetting: false
            },
            {
                id: 3,
                name: CALENDAR_DAY_TYPE.SICK.toString(),
                value: "Больничный",
                officeCalendarSetting: false,
                personalCalendarSetting: true
            },
            {
                id: 4,
                name: CALENDAR_DAY_TYPE.VACATION.toString(),
                value: "Отпуск",
                officeCalendarSetting: false,
                personalCalendarSetting: true
            }
        ]
    },
    {
        id: 3,
        name: "testNoResponseMinutes",
        type: 'number',
        title: "Waiting time for a response to the check (min)",
        isCustomizable: true,
        value: 20
    },
    {
        id: 4,
        name: "testSuccessfulMinutes",
        type: 'number',
        title: "Acceptable response time for verification (min)",
        isCustomizable: true,
        value: 16
    },
];

export default DEFAULT_SETTINGS_LIST;
