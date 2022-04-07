import React, {useEffect} from "react";
import {Field} from "formik";
import {AppRootState, TypedSelector} from '../../../store';
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import {useTranslation} from "react-i18next";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {Tab, Tabs, TextField} from "@material-ui/core";
import TransferList from './TransferList/TransferList';
import {buttonStyle} from "../../../common/styles";
import {hasAccessEditProjectsSelector} from "../../employeesCheck/selectors";
import {connect} from "react-redux";
import Alert from "@material-ui/lab/Alert";
import StoredSelector from "../../../components/misc/StoredSelector/StoredSelector";

const useStyles = makeStyles((theme: Theme): { root: any } =>
    createStyles({
        root: {},
    }),
);

interface TabPanelProps {
    children?: React.ReactNode;
    index: any;
    value: any;
    hasAccessEditProjects?: boolean;

}

function TabPanel(props: TabPanelProps) {
    const {children, value, index, hasAccessEditProjects, ...other} = props;
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

const EditorForm = (props: any) => {
    const {
        onCancel,
        handleSubmit,
        isValid,
        isEdit,
        values,
    } = props;
    const employeeList = TypedSelector(state => state.references).refs['employee']?.list || [];
    let availableUsers = [...employeeList];
    const [emptyListNotation, setEmptyListNotation] = React.useState(true);
    const [selectedUsers, setSelectedUsers] = React.useState<Array<any>>([]);
    const [usersInWork, setUsersInWork] = React.useState<Array<any>>([]);
    useEffect(() => {
        if (values?.employeeIds.length > 0) {
            setEmptyListNotation(false)
        } else {
            setEmptyListNotation(true)
        }
    }, [values?.employeeIds.length]);
    useEffect(() => {
        let selectedUsersList: any[] = [];
        if (Array.isArray(availableUsers)) {
            values?.employeeIds?.forEach((value: string) => {
                let index = availableUsers.findIndex(item => item.id === value);
                if(availableUsers[index]) {
                    selectedUsersList.push(availableUsers[index]);
                } else {
                    let index = usersInWork.findIndex(item => item.id === value);
                    selectedUsersList.push(usersInWork[index]);
                }
            })
        }
        setSelectedUsers(selectedUsersList);
    }, [values.employeeIds]);

    const addUsersInWork = (users: any[]) => {
        setUsersInWork(users);
    }
    const classes = useStyles();
    const [activeTabValue, setActiveTabValue] = React.useState(0);

    const {t} = useTranslation();
    const fieldAdditionStyle = {marginBottom: "25px"}

    const handleTabChange = (event: React.ChangeEvent<{}>, ActiveTabValue: number) => {
        setActiveTabValue(ActiveTabValue)
    };
    return (
        <form
            className={classes.root}
            onSubmit={handleSubmit}
        >
            <Alert
                style={{
                    visibility: (emptyListNotation ? "visible" : "hidden"),
                }}
                severity="warning">Заполните список сотрудников</Alert>
            <DialogContent style={{width: 1200}}>
                <Tabs value={activeTabValue} onChange={handleTabChange} style={{marginBottom: 5}}>
                    <Tab label={t('General information')}/>
                    <Tab label={t('Employees')}/>
                    <Tab label={t('Project manager')}/>
                </Tabs>
                <TabPanel
                    value={activeTabValue} index={0}>
                    <Field
                        id="name"
                        name="name"
                        disabled
                        render={({field, meta}: any) => {
                            return <TextField {...field}
                                              label={`${t("Project name")} *`}
                                              fullWidth
                                              helperText={meta.touched && meta.error}
                                              error={meta.touched && Boolean(meta.error)}
                            />

                        }}
                    />

                </TabPanel>
                <TabPanel
                    value={activeTabValue} index={1}>
                    <TransferList
                        name="employeeIds"
                        selected={selectedUsers}
                        addUsersInWork={addUsersInWork}
                    />
                </TabPanel>
                <TabPanel index={2} value={activeTabValue}>
                    <StoredSelector
                        store={selectedUsers}
                        name="pmIds"
                        emptyString={true}
                        label={t("Project manager")}
                        style={fieldAdditionStyle}
                        multiple
                        autocomplete
                    />
                </TabPanel>
            </DialogContent>
            <DialogActions>
                <Button
                    onClick={() => {
                        onCancel();
                    }}
                    color="primary"
                >
                    {t('Cancel')}
                </Button>
                <Button style={buttonStyle} type="submit" color="secondary" variant="contained"
                        disabled={!isValid}>
                    {isEdit ? t('Save') : t('Add')}
                </Button>
            </DialogActions>
        </form>
    );
};
const mapStateToProps = (state: AppRootState) => ({
    hasAccessEditProjects: hasAccessEditProjectsSelector(state),
})

const connector = connect(mapStateToProps)

export default connector(EditorForm)
