import React from 'react';
import Combobox from '../../../components/misc/Combobox/Combobox';
import { useTranslation } from "react-i18next";
import {MenuItem} from "@material-ui/core";

export interface ProjectPickerProps {
    onChange?: any;
    defaultValue?: any;
    value?: any;
    values: [];
}

const ProjectPicker = ({
    value,
    defaultValue,
    onChange,
    values = [],
}: ProjectPickerProps) => {
    const { t } = useTranslation();

    const handlePickerChange = (value: any) => {
        if (typeof onChange == "function") {
            onChange(value);
        }
    }

    const picker = <Combobox
        values={values}
        onChange={handlePickerChange}
        refName='projects'
        defaultValue={defaultValue || ""}
        value={value}
        displayField="name"
        valueField="id"
        listTitle={t("Projects")}
    >
        <MenuItem value={t("Choose project")}>
            <em>{t('None')}</em>
        </MenuItem>
        {(values || []).map(({ id, name }: any) => <MenuItem
            value={id}>{name}</MenuItem>)}
    </Combobox>;
    return picker;
}

export default ProjectPicker;