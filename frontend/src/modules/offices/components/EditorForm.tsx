import React from "react";
import {Field} from "formik";
import {MenuItem, TextField} from "@material-ui/core";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import {buttonStyle} from "../../../common/styles";
import {Timezones} from "../constants";
import {AppRootState} from "../../../store";
import {hasAccessEditOfficesSelector} from "../../employeesCheck/selectors";
import {connect} from "react-redux";


const useStyles = makeStyles((theme: Theme): { root: any } =>
    createStyles({
        root: {},
    }),
);

const EditorForm = (props: any) => {
    const classes = useStyles();
    const {
        onCancel,
        handleSubmit,
        isValid,
        isEdit,
        calendars,
        hasAccessEditOffice
    } = props;
    const {t} = useTranslation();

    return (
        <form
            className={classes.root}
            onSubmit={handleSubmit}
        >
            <DialogContent
                style={{pointerEvents: hasAccessEditOffice || !isEdit ? "auto" : "none"}}
            >
                <Field
                    key="name"
                    name="name"
                    render={({field, meta}: any) => {
                        return <TextField {...field}
                                          label={`${t("Office name")}`}
                                          fullWidth
                                          helperText={meta.touched && meta.error}
                                          error={meta.touched && Boolean(meta.error)}
                        />
                    }}

                />

                <Field
                    key="calendarId"
                    name="calendarId"
                    render={({field, meta}: any) => {
                        return <TextField
                            {...field}
                            label={t("Calendar")}
                            fullWidth
                            select
                            helperText={meta.touched && meta.error}
                            error={meta.touched && Boolean(meta.error)}
                        >
                            <MenuItem value="">
                                <em>{t('None')}</em>
                            </MenuItem>
                            {calendars.map(({id, name}: any) => <MenuItem
                                value={id}>{name}</MenuItem>)}
                        </TextField>
                    }}
                />

                <Field
                    key="timezone"
                    name="timezone"
                    render={({field, meta}: any) => {
                        return <TextField
                            {...field}
                            label={t("timezone ")}
                            fullWidth
                            select
                            helperText={meta.touched && meta.error}
                            error={meta.touched && Boolean(meta.error)}
                        >
                            <MenuItem value="">
                                <em>{t('None')}</em>
                            </MenuItem>
                            {Timezones.map((tz) => {
                                return <MenuItem value={tz.value}>{tz.name}</MenuItem>
                            })}

                        </TextField>
                    }}

                />

            </DialogContent>
            <DialogActions>
                <Button
                    onClick={() => {
                        onCancel();
                    }}
                    color="primary"
                >
                    {hasAccessEditOffice || !isEdit ? t('Cancel') : t('Close')}
                </Button>
                {hasAccessEditOffice || !isEdit ? <Button style={buttonStyle} type="submit" color="secondary" variant="contained" disabled={!isValid}>
                    {isEdit ? t('Save') : t('Add')}
                </Button> : null }
            </DialogActions>
        </form>
    );
};

const mapStateToProps = (state: AppRootState) => ({
    hasAccessEditOffice: hasAccessEditOfficesSelector(state),
})

const connector = connect(mapStateToProps)

export default connector(EditorForm)
