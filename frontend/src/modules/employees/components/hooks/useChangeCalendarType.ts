import {getEntityById} from "../../../../store";

const useChangeCalendarType = (setEntityData: (newEntityData: any) => void) => {
    return async (id: string, isCustom: boolean, entityData: any) => {
        const changeCalendar = async (calendarId: string, entityData: any) => {
            let newEntityData = {...entityData};
            await getEntityById(calendarId, 'calendars', (data: any) => {
                newEntityData.events = [...data.events];
                newEntityData.calendarId = data.id;
                newEntityData.calendarName = data.name;
                newEntityData.startWeekDay = data.startWeekDay;
                newEntityData.weekendDays = data.weekendDays;
                newEntityData.workHoursFrom = data.workHoursFrom;
                newEntityData.workHoursTo = data.workHoursTo;
            });
            setEntityData(newEntityData);
        }
        let newEntityData = {...entityData};
        newEntityData.isCustomCalendar = isCustom;
        if (id) {
            newEntityData.baseOffice = id;
            await getEntityById(id, 'offices', (data: any) => {
                newEntityData.calendarId = data.calendarId;
                newEntityData.officeCalendarId = data.calendarId;
            });
        }
        setEntityData(newEntityData);
        if (id && !isCustom) {
            changeCalendar(newEntityData.calendarId, newEntityData);
        }
        if (!id && isCustom) {
            if (!newEntityData.customCalendarId) { return }
            changeCalendar(newEntityData.customCalendarId, newEntityData);
        }
        if (!id && !isCustom) {
            if (!newEntityData.officeCalendarId) { return }
            changeCalendar(newEntityData.officeCalendarId, newEntityData);
        }
    }
}

export default useChangeCalendarType;
