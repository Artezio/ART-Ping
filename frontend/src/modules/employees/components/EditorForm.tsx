import React from "react";
import {
    TextField,
    Tabs,
    Tab
} from "@material-ui/core";
import {Field, useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {CombinedFormComponent as CalendarField} from "../../../components/misc/Calendar/CombinedFormComponent";
import StoredSelector from "../../../components/misc/StoredSelector/StoredSelector";
import {buttonStyle} from "../../../common/styles";
import EditorFormRoles from './EditorFormRoles';
import {hasAccessAddEditGraphicsSelector} from "../../employeesCheck/selectors";
import {useSelector} from "react-redux";

const useStyles = makeStyles((_: Theme): { root: any } =>
    createStyles({
        root: {},
    }),
);

interface TabPanelProps {
    children?: React.ReactNode;
    index: any;
    value: any;
}

function TabPanel(props: TabPanelProps) {
    const {children, value, index, ...other} = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (<>{children}</>)}
        </div>
    );
}

export interface IEditorFormProps {
    onCancel(): void,

    handleSubmit?: (data: any) => void,
    onChange?: (id: any, isCustom: any, formValues: any) => void,
    isValid?: boolean,
    isEdit?: boolean,
    errors?: any,
    readOnly?: boolean,

}

export const EditorForm = ({
                               onCancel,
                               handleSubmit,
                               onChange,
                               isValid,
                               isEdit = false,
                               readOnly = false
                           }: IEditorFormProps) => {

    const hasAccessAddEditGraphics = useSelector(hasAccessAddEditGraphicsSelector)
    const classes = useStyles();
    const {t} = useTranslation();

    const [activeTabValue, setActiveTabValue] = React.useState(0);
    const {values} = useFormikContext();
    const {isCustomCalendar} = (values as any);
    const handleTabChange = (_: any, ActiveTabValue: number) => {
        setActiveTabValue(ActiveTabValue)
    };


    const handleChangeCalendar = (officeId: string, isCustom: boolean, formValues: any) => {
        onChange && onChange(officeId, isCustom, formValues)
    }
    const handleChangeOffice = (officeId: string) => {
        handleChangeCalendar(officeId, isCustomCalendar, values);
    }
    const handleChangePersonalCB = () => {
        (isCustomCalendar) ? handleChangeCalendar('', false, values) : handleChangeCalendar('', true, values);
    }
    return (
        <form
            className={classes.root}
            onSubmit={handleSubmit}
            style={{width: "1000px", display: "flex", height: "710px", flexDirection: "column",}}
        >
            <DialogContent>

                <Tabs value={activeTabValue} onChange={handleTabChange} style={{marginBottom: 5}}>
                    {/*<div  style={{pointerEvents: !readOnly ? "auto" : "none"}}>*/}
                    <Tab label={t("General information")}/>
                    <Tab label={t("Schedule")} disabled={!hasAccessAddEditGraphics}/>
                    <Tab label={t("Roles")}/>
                </Tabs>
                <div style={{pointerEvents: !readOnly ? "auto" : "none"}}>
                    <TabPanel
                        value={activeTabValue} index={0}>
                        <Field
                            name="firstName"
                            render={({field, meta}: any) => {
                                return <TextField
                                    {...field}

                                    label={`${t('First name')} *`}
                                    fullWidth
                                    helperText={meta.touched && meta.error}
                                    error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />

                        <Field
                            name="lastName"
                            render={({field, meta}: any) => {
                                return <TextField {...field}
                                                  label={`${t('Last name')} *`}
                                                  fullWidth
                                                  helperText={meta.touched && meta.error}
                                                  error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />
                        <Field
                            name="email"
                            render={({field, meta}: any) => {
                                return <TextField {...field}

                                                  label={`${t("Email")} *`}
                                                  fullWidth
                                                  helperText={meta.touched && meta.error}
                                                  error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />
                        <Field
                            name="login"
                            render={({field, meta}: any) => {
                                return <TextField {...field}
                                                  label={`${t('Login')} *`}
                                                  fullWidth
                                                  helperText={meta.touched && meta.error}
                                                  error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />
                        {!isEdit ?
                            <Field
                                name="password"
                                render={({field, meta}: any) => {
                                    return <TextField {...field}
                                                      label={t('Password')}
                                                      fullWidth
                                                      helperText={meta.touched && meta.error}
                                                      error={meta.touched && Boolean(meta.error)}
                                    />
                                }}

                            /> : null
                        }

                        <StoredSelector
                            store="offices"
                            name="baseOffice"
                            autocomplete
                            emptyString
                            label={`${t('Office')} *`}
                            onChange={handleChangeOffice}
                        />

                </TabPanel>
                </div>
                <TabPanel value={activeTabValue} index={1}>
                    <CalendarField allowCustom
                                   onChange={handleChangePersonalCB}
                                   readOnly={readOnly}
                    />
                </TabPanel>
                <div style={{pointerEvents: !readOnly ? "auto" : "none"}}>
                <TabPanel value={activeTabValue} index={2}>
                    <EditorFormRoles/>
                </TabPanel>
                </div>

            </DialogContent>
            <DialogActions>
                <Button
                    style={buttonStyle}
                    onClick={() => {
                        onCancel();
                    }}
                    color="primary"
                >
                    {t('Close')}
                </Button>
                {!readOnly &&
                <Button style={buttonStyle} type="submit" color="secondary" variant="contained" disabled={!isValid}>
                    {isEdit ? t('Save') : t('Add')}
                </Button>}
            </DialogActions>
        </form>
    );
};
