import React from 'react';
import Combobox from '../../../components/misc/Combobox/Combobox';
import {useTranslation} from "react-i18next";
import {MenuItem} from "@material-ui/core";

export interface OfficePickerProps {
    onChange?: any;
    defaultValue?: any;
    value?: any;
    values: [];
}

const OfficePicker = ({

                    value,
                    defaultValue,
                    onChange,
                    values = [],
                }: OfficePickerProps) => {
    const {t} = useTranslation();

    const handlePickerChange = (value: any) => {
        if (typeof onChange == "function") {
            onChange(value);
        }
    }

    const picker = <Combobox
        values={values}
        onChange={handlePickerChange}
        refName='offices'
        defaultValue={defaultValue || ""}
        value={value}
        displayField="name"
        valueField="id"
        listTitle={t("Office")}
    >
        <MenuItem value={t("Choose office")}>
            <em>None</em>
        </MenuItem>
        {values.map(({id, name}: any) => <MenuItem
            value={id}>{name}</MenuItem>
        )}
    </Combobox>;
    return picker;
}

export default OfficePicker