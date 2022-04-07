import React from 'react';
import Combobox from '../../../components/misc/Combobox/Combobox';
import { useTranslation } from "react-i18next";

export interface CalendarPickerProps {
    onChange?: any;
    defaultValue?: any;
    value?: any;
    // okLabel?:any;
    // cancelLabel?:any;
}

const CalendarPicker = (props: CalendarPickerProps) => {
    const { t } = useTranslation();

    const handlePickerChange = (value: any) => {
        if (typeof props.onChange == "function") {
            props.onChange(value);
        }
    }

    const picker = <Combobox
        onChange={handlePickerChange}
        refName='calendars'
        defaultValue={props.defaultValue || ""}
        value={props.value}
        displayField="name"
        valueField="id"
        listTitle={t("Calendar")}


    />;
    return picker;
}

export default CalendarPicker;