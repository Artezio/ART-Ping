import React from 'react';
import Combobox from '../../../components/misc/Combobox/Combobox';
import { useTranslation } from "react-i18next";

export interface UsersPickerProps {
    onChange?: any;
    defaultValue?: any;
    value?: any;
}

const UsersPicker = (props: UsersPickerProps) => {
    const { t } = useTranslation();

    const handlePickerChange = (value: any) => {
        if (typeof props.onChange == "function") {
            props.onChange(value);
        }
    }

    const picker = <Combobox
        onChange={handlePickerChange}
        refName='users'
        defaultValue={props.defaultValue || ""}
        value={props.value}
        displayField="name"
        valueField="id"
        listTitle={t("Users")}
    />;
    return picker;
}

export default UsersPicker;