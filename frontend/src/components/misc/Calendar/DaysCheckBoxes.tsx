import React from 'react';
import { Field, useFormikContext } from "formik";
import { FormControl, FormGroup, FormLabel } from "@material-ui/core";
import { CheckboxWithLabel } from "formik-material-ui";
import { WEEK_DAYS } from './src/shared';
import { remove } from "ramda";
import { boolean } from 'yup/lib/locale';

export interface DaysCheckBoxesProps {
    disabled?: boolean,
    prefix: string,
    onChange?: (e: any) => void,
    currentWeekends?: number[] | string[],
    legend?: string,
    weekStartsOn?: 0 | 1 | 2 | 3 | 4 | 5 | 6
    form?: any
}

const DaysCheckBoxes = ({
    disabled,
    prefix,
    onChange,
    legend,
    weekStartsOn = 1
    //currentWeekends = []
}: DaysCheckBoxesProps) => {
    const fields: any[] = [];
    const { values, setFieldValue } = useFormikContext<any>();



    const prepareCheckbox = (i: number) => {
        return <div key={`day_checkbox_${prefix}_${i}`}><Field
            type="checkbox"
            component={CheckboxWithLabel}
            name={prefix}
            disabled={disabled}
            //checked={isChecked}
            key={i}
            value={i}
            label={{ label: WEEK_DAYS[i] }}
            onChange={(ev: any, checked: any) => {
                let value: any[] = (values.hasOwnProperty(prefix) && values[prefix] != null) ? [...values[prefix]] : [];
                if (checked) {
                    value.push(i);
                } else {
                    value = remove(value.indexOf(i), 1, value);
                }
                if (typeof onChange == "function") {
                    onChange(ev);
                }
                return setFieldValue(prefix, value);
            }}
        />{WEEK_DAYS[i]}</div>
    }

    for (let i = weekStartsOn; i < WEEK_DAYS.length; i++) { fields.push(prepareCheckbox(i)) }
    for (let i = 0; i < weekStartsOn; i++) { fields.push(prepareCheckbox(i)) }

    return <FormControl component="fieldset" style={{ display: "flex" }}>
        {legend && <FormLabel component="legend">{legend}</FormLabel>}
        <FormGroup>
            {fields}
        </FormGroup>
    </FormControl>;
}

export default DaysCheckBoxes;