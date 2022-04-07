import React from "react";
import {Field, useFormikContext} from "formik";
import {TextField} from "@material-ui/core";
import {CheckboxWithLabel} from "formik-material-ui";
import {useTranslation} from "react-i18next";
import DaysCheckBoxes from "./DaysCheckBoxes";
import Calendar from "./Calendar";
import {WEEKEND_DAYS} from "./src/shared";


export type ICombinedFormComponentProps = {
    withName?: boolean;
    allowCustom?: boolean;
    onChange?: () => void;
    readOnly?: boolean;
}

export const CombinedFormComponent = (props: ICombinedFormComponentProps) => {

    const {t} = useTranslation();
    const {values} = useFormikContext<any>();
    const [checkedWeekends, setCheckedWeekends] = React.useState(WEEKEND_DAYS);
    const [formFieldsValue, setFormFieldsValue] = React.useState({
        name: "",
        workHoursFrom: "",
        workHoursTo: "",
        startWeekDay: 1,
    });

    const onWeekendsDaysCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const dayId: number = Number(event.target.value);
        const isChecked = event.target.checked;
        let newCheckedWeekends = [...checkedWeekends];
        if (isChecked) {
            newCheckedWeekends[dayId] = dayId;
        } else {
            newCheckedWeekends = checkedWeekends.filter(val => val !== dayId);
        }
        setCheckedWeekends(newCheckedWeekends);
    }


    const fieldChangeHandler = (event: any, fieldType: string) => {
        switch (fieldType) {
            case "text":
                setFormFieldsValue({
                    ...formFieldsValue,
                    [event.name]: event.value
                })
                break;
            case "calendar":
                setFormFieldsValue({
                    ...formFieldsValue,
                    [event.name]: event
                })
                break;
            default:
                break;
        }
    }

    return <div style={{display: "flex"}}>
        <div
            className="calendar-props-row"
            style={{pointerEvents: !props.readOnly ? "auto" : "none"}}>
            {props.allowCustom && <>
                <Field
                    type="checkbox"
                    component={CheckboxWithLabel}
                    name="isCustomCalendar"
                    onClick={() => props.onChange && props.onChange()}
                />{t("Personal calendar")}</>}
            {props.withName &&
            <Field
                id="name"
                name="name"
                render={({field, meta}: any) => {
                    return <TextField {...field}
                                      name="name"
                                      label={`${t("Name")} *`}
                                      fullWidth
                                      helperText={meta.touched && meta.error}
                                      error={meta.touched && Boolean(meta.error)}
                    />
                }}
            />}
            <Field
                id="workHoursFrom"
                name="workHoursFrom"
                render={({field, meta}: any) => {
                    return <TextField {...field}
                                      disabled={props.allowCustom && !values.isCustomCalendar}
                                      name="workHoursFrom"
                                      label={`${t("Work hours from")} *`}
                                      fullWidth
                                      helperText={meta.touched && meta.error}
                                      error={meta.touched && Boolean(meta.error)}
                                      type="time"
                                      inputProps={{
                                          step: 600, // 10 min
                                      }}
                    />
                }}
            />
            <Field
                id="workHoursTo"
                name="workHoursTo"
                render={({field, meta}: any) => {
                    return <TextField {...field}
                                      disabled={props.allowCustom && !values.isCustomCalendar}
                                      label={`${t("Work hours to")} *`}
                                      name="workHoursTo"
                                      fullWidth
                                      helperText={meta.touched && meta.error}
                                      error={meta.touched && Boolean(meta.error)}
                                      type="time"
                                      inputProps={{
                                          step: 600, // 10 min
                                      }}
                    />
                }}
            />
            <h2>Выходные дни</h2>
            <div role="group" aria-labelledby="checkbox-group">
                <DaysCheckBoxes
                    disabled={props.allowCustom && !values.isCustomCalendar}
                    prefix={'weekendDays'}
                    onChange={onWeekendsDaysCheckboxChange}
                    currentWeekends={checkedWeekends}
                />
            </div>
        </div>


        <Field
            id="events"
            name="events"
            render={({field}: any) => {
                return <Calendar {...field}
                                 readOnly={props.readOnly}
                                 disabled={props.allowCustom && !values.isCustomCalendar}
                                 weekendDays={checkedWeekends}
                                 enableReinitialize={true}
                                 isCustom={props.allowCustom}
                                 onChange={(e: any) => fieldChangeHandler(e, "calendar")}
                />
            }}
        />
    </div>
}
