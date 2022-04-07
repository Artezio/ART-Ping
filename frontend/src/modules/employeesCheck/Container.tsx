import React, {useEffect, useMemo, useState} from 'react';
import {connect, ConnectedProps, useSelector} from 'react-redux';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import Button from '@material-ui/core/Button';
import {AutoCheckForm, DateRange, EmployeesTable, SearchTextInput} from './components';
import {useTranslation} from "react-i18next";
import {AppRootState, prepareFetchParams, TypedSelector} from '../../store';
import {Backdrop, Checkbox, CircularProgress, FormControlLabel, FormGroup} from "@material-ui/core";
import {cancelChecksPeriodAction, checkNowForEmployees, filterEmployeeChecks, insertPlanForEmployees} from "./actions";
import {DownloadButtonInterface, EmployeeCheckFilterUIInterface, PlanForEmployeesInterface} from "./types";
import {
    getEmployeeChecks,
    getTotal,
    hasAccessCancelChecksSelector,
    hasAccessEmployeesCheckSelector, hasAccessOfficesSelector, hasAccessProjectsSelector,
    isEmployeeChecksLoading
} from "./selectors";
import {format} from "date-fns";
import CheckNowForm from "./components/CheckNowForm";
import Dropdown from "../../components/misc/Dropdown/Dropdown";
import SearchIcon from '@material-ui/icons/Search';
import {buttonDisabledStyle, buttonStyle} from '../../common/styles';
import DownloadButton from "./components/DownloadButton"
import {API_PREFIX} from "../../constants";
import {getDefaultPeriod} from "./utils";
import {getCurrentUser} from "../../store/user/selectors";
import {DialogConfirm} from "../../common/dialogs/DialogConfirm";
import {ADMIN, DIRECTOR, PM} from "../../store/user/constants";

interface Props extends PropsFromRedux {
    disableSelectionOnClick?: boolean;
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
        listFilters: any,
        listActions: any,
        flexGrow: any,
        searchBar: any,
        dataGridWrapper: any,
        dataGrid: any,
        backdrop: any,
        dropdown: any,
        searchButton: any,
        dialogConfirm: any,
        dialogEdit: any,
    } =>
        createStyles({
            root: {
                height: "calc(100% - 90px)",
                width: '100%',
            },
            listFilters: {},
            searchButton: {color: 'black'},
            listActions: {
                backgroundColor: "#FFFFFF"
            },
            flexGrow: {
                flexGrow: 1,
                display: "flex"
            },
            searchBar: {
                flexGrow: 1
            },
            dropdown: {
                flexGrow: 1,
                display: "flex",
                marginTop: "-16px"
            },
            dataGridWrapper: {
                height: "calc(100% - 110px)",
            },
            dataGrid: {
                backgroundColor: "#FFFFFF",

            },
            dialogConfirm: {},
            dialogEdit: {},
            backdrop: {
                zIndex: theme.zIndex.drawer + 1,
                color: '#fff',
            },

        })
);

const Container = ({
                       onCancelChecksPeriod,
                       onCheckNowForEmployees,
                       onInsertPlanForEmployees,
                       isEmployeeChecksLoading,
                       disableSelectionOnClick = true,
                       employeeChecks = [],
                       onFilterEmployeeChecks,
                       total = 0,
                       currentUser
                   }: Props) => {
    const classes = useStyles();
    const {t} = useTranslation();


    const defaultFilterState: EmployeeCheckFilterUIInterface = {
        officeId: null,
        projectId: null,
        searchString: "",
        period: getDefaultPeriod(),
        roleName: null,
        pageNumber: 0,
        pageSize: 20,
        sort: [],
        onlyActiveEmployees: true,

    };

    useEffect(() => {
        if (onFilterEmployeeChecks) {
            onFilterEmployeeChecks(defaultFilterState)
        }
    }, [onFilterEmployeeChecks]);

    const [filtersState, setFiltersState] = React.useState<EmployeeCheckFilterUIInterface>(defaultFilterState);
    const [selectionModel, setSelectionModel] = useState<Array<string>>([]);
    const hasAccessToProjectsSelector = useSelector(hasAccessProjectsSelector);
    const hasAccessToEmployeesChecks = useSelector(hasAccessEmployeesCheckSelector);
    const hasAccessToOfficesSelector = useSelector(hasAccessOfficesSelector);
    const hasAccessCancelChecks = useSelector(hasAccessCancelChecksSelector)
    const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
    const [isConfirmationData, setConfirmationData] = useState<any>({mode: "", data: {}});
    useEffect(() => {
        handlers.searchSubmit();
    }, [filtersState.sort]);

    const isCheckDisable = useMemo(() => {
        if (selectionModel.length === 0)
            return true;

        const selectedEmployeeChecks = employeeChecks.filter(function (employeeCheck: any) {
            return selectionModel.indexOf(employeeCheck.id) !== -1
        });

        return selectedEmployeeChecks.find(function (selectedEmployeeCheck: any) {
            return !selectedEmployeeCheck.active
        });
    }, [employeeChecks, selectionModel]);

    const references = TypedSelector((state: any) => {
        return state.references.refs;
    });
    const getCurrentProjects = useMemo(() => {
        const projects = references['projects']?.list ?? [];
        const comparisonProjectIds = projects.filter((project: any) => currentUser.managedProjects.indexOf(project.id) !== -1);
        if (currentUser.roles.indexOf(PM) !== -1 && currentUser.roles.length === 1) {
            return comparisonProjectIds;
        }
        ;
        return projects;

    }, [references['projects'], currentUser.roles, currentUser.managedProjects]);

    const getCurrentOffices = useMemo(() => {
        const offices = references['offices']?.list ?? [];
        const comparisonOfficeIds = offices.filter((office: any) => currentUser.managedOffices.indexOf(office.id) !== -1);
        if (currentUser.roles.indexOf(ADMIN) !== -1) {
            return offices;
        }
        ;
        if (currentUser.roles.indexOf(DIRECTOR) !== -1) {
            return comparisonOfficeIds;
        }
        ;
        return offices;
    }, [references['offices'], currentUser.roles, currentUser.managedOffices]);

    const handlers = {
        handleSortModelChange: (newModel: any) => {
            setFiltersState({
                ...filtersState,
                sort: newModel
            });
        },
        onlyActiveEmployees: ({target: {checked}}: any) => {
            setFiltersState({
                ...filtersState,
                onlyActiveEmployees: checked
            });
        },
        cancelGroupChecks: () => {
            setIsConfirmOpen(true);
            setConfirmationData({mode: "multi", data: selectionModel});
        },
        dateRange: (value: any) => {
            const fs = {...filtersState, period: {...value}};
            setFiltersState(fs);
            onFilterEmployeeChecks(fs);
        },
        searchString: (value: string) => {
            setFiltersState({
                ...filtersState,
                searchString: value
            });
        },
        office: (value: any) => {
            setFiltersState({
                ...filtersState,
                officeId: value
            });
        },
        project: (value: any) => {
            setFiltersState({
                ...filtersState,
                projectId: value
            });
        },
        searchSubmit: () => {
            let fs = {...filtersState, pageNumber: 0};
            setFiltersState(fs);
            onFilterEmployeeChecks(fs);
        },
        filtersReset: () => {
            const filter = {...defaultFilterState};
            setFiltersState(filter);
            onFilterEmployeeChecks(filter);
        },
        select: (newSelection: any) => {
            setSelectionModel(newSelection);
        },
        handlePageChange: (params: any) => {
            let fs = {...filtersState, pageNumber: params};
            setFiltersState(fs);
            onFilterEmployeeChecks(fs);
        },
        handlePageSizeChange: (itemsOnPage: number) => {
            let fs = {...filtersState, pageSize: itemsOnPage, pageNumber: 0};
            setFiltersState(fs);
            onFilterEmployeeChecks(fs);
        },
        setAutoCheck: (values: any) => {
            const newPlan = {
                dailyChecks: values.dailyChecks,
                startDate: format(filtersState.period.from, "yyyy-MM-dd"),
                endDate: format(filtersState.period.to, "yyyy-MM-dd"),
                ids: [...selectionModel],

            }
            onInsertPlanForEmployees(newPlan, filtersState);
        },
        checkNow: () => {
            onCheckNowForEmployees([...selectionModel], {
                ...filtersState,
                period: {
                    ...filtersState.period,
                    from: filtersState.period.from,
                    to: filtersState.period.to
                }
            });
        },


        downloadButtonHandler: () => {
            const params: DownloadButtonInterface = {
                employeeIds: [...selectionModel],
                startPeriod: format(filtersState.period.from, "yyyy-MM-dd"),
                endPeriod: format(filtersState.period.to, "yyyy-MM-dd"),
            }
            fetch(`${API_PREFIX}/export`, prepareFetchParams({
                    method: "POST", body: JSON.stringify(params),
                })
            ).then((response) => response.blob())

                .then((blob) => {

                    const url = window.URL.createObjectURL(
                        new Blob([blob]),
                    );
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute(
                        'download',
                        `Statistics.xlsx`,
                    );
                    document.body.appendChild(link);
                    link.click();
                })
        },
        cancelEmployeeChecksConfirm: () => {
            onCancelChecksPeriod([...selectionModel], {...filtersState});
        },
    }


    const isCheckNowDisabled = useMemo(() => {
        if (selectionModel.length === 0)
            return true;

        const selectedEmployeeChecks = employeeChecks.filter(function (employeeCheck: any) {
            return selectionModel.indexOf(employeeCheck.id) !== -1
        });

        return selectedEmployeeChecks.find(function (selectedEmployeeCheck: any) {
            return !selectedEmployeeCheck.active
        });
    }, [employeeChecks, selectionModel]);

// return selectionModel.length > 0 ? false : true
    return <div className={classes.root}>
        {<Toolbar
            className={classes.listFilters}
        >
            <DateRange {...filtersState.period}
                       onChange={handlers.dateRange}
            />
            <SearchTextInput
                value={filtersState?.searchString || ""}
                onChange={handlers.searchString}
            />
            {hasAccessToOfficesSelector ? <div
                className={classes.dropdown}
            >
                <Dropdown
                    label={t("Offices")}
                    defaultValue={defaultFilterState?.officeId || null}
                    value={filtersState?.officeId || null}
                    values={getCurrentOffices}
                    onChange={handlers.office}
                    // references['offices']?.list ?? []
                />
            </div> : null}
            <FormGroup>
                <FormControlLabel style={{marginLeft: "10px"}} control={<Checkbox
                    checked={filtersState?.onlyActiveEmployees || false}
                    onChange={handlers.onlyActiveEmployees}
                />} label={t("Active")}/>
            </FormGroup>
            {hasAccessToProjectsSelector ? <div
                className={classes.dropdown}
            >
                <Dropdown
                    label={t("Projects")}
                    defaultValue={defaultFilterState?.projectId || null}
                    values={getCurrentProjects}
                    value={filtersState?.projectId || null}
                    onChange={handlers.project}
                />
            </div> : null}

            <Button
                style={buttonStyle}
                className={classes.searchButton}
                color="secondary"
                variant="outlined"
                onClick={handlers.searchSubmit}
            ><SearchIcon/>{t('Search')}</Button>
            <Button
                style={buttonStyle}
                color="secondary"
                onClick={handlers.filtersReset}
            >{t('Reset filters')}</Button>
        </Toolbar>}

        <Toolbar
            className={classes.listActions}
        >
            <div
                className={classes.flexGrow}
            >
                <CheckNowForm
                    disabled={isCheckNowDisabled}
                    checkNow={handlers.checkNow}
                    selected={selectionModel}
                    employeeChecks={employeeChecks}
                    hasAccess={!hasAccessToEmployeesChecks}
                />
                <AutoCheckForm
                    disabled={isCheckDisable}
                    setAutoCheck={handlers.setAutoCheck}

                    selected={selectionModel}
                    hasAccess={!hasAccessToEmployeesChecks}
                />

                <Button
                    disabled={!hasAccessCancelChecks}
                    style={hasAccessCancelChecks ? buttonStyle : buttonDisabledStyle}
                    variant="outlined"
                    onClick={handlers.cancelGroupChecks}
                >{t('Cancel autoCheck')}</Button>

                <DownloadButton
                    selected={selectionModel}
                    download={handlers.downloadButtonHandler}
                    hasAccess={hasAccessToEmployeesChecks}
                />


            </div>
        </Toolbar>
        <div className={classes.dataGridWrapper}>
            <EmployeesTable
                sortingMode="server"
                onSortModelChange={handlers.handleSortModelChange}
                disableColumnMenu
                page={filtersState.pageNumber}
                rows={employeeChecks || []}
                startPeriod={filtersState.period.from}
                endPeriod={filtersState.period.to}
                onSelectionModelChange={handlers.select}
                disableSelectionOnClick={disableSelectionOnClick}
                selectionModel={selectionModel}
                total={total}
                handlePageChange={handlers.handlePageChange}
                onItemsOnPageChange={handlers.handlePageSizeChange}
                filters={filtersState}
            />

        </div>
        <DialogConfirm
            open={isConfirmOpen}
            onYesClick={() => {
                setIsConfirmOpen(false);
                handlers.cancelEmployeeChecksConfirm();
            }}
            onNoClick={() => {
                setIsConfirmOpen(false)
            }}/>

        <Backdrop
            className={classes.backdrop}
            open={isEmployeeChecksLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </div>
}


const mapStateToProps = (state: AppRootState) => {
    return ({
        employeeChecks: getEmployeeChecks(state),
        isEmployeeChecksLoading: isEmployeeChecksLoading(state),
        total: getTotal(state),
        currentUser: getCurrentUser(state),
        isListEmployeesCheckDisabled: hasAccessEmployeesCheckSelector(state),
    });
}


const mapDispatchToProps = (dispatch: any) => {
    return {
        onFilterEmployeeChecks: (filter: EmployeeCheckFilterUIInterface) => {
            dispatch(filterEmployeeChecks(filter));
        },
        onInsertPlanForEmployees: (newPlan: PlanForEmployeesInterface, filter: EmployeeCheckFilterUIInterface) => {
            dispatch(insertPlanForEmployees(newPlan, filter));
        },
        onCheckNowForEmployees: (checkedEmployees: Array<string>, filter: EmployeeCheckFilterUIInterface) => {
            dispatch(checkNowForEmployees(checkedEmployees, filter));
        },
        onCancelChecksPeriod: (selectedEmployee: Array<string>, filter: EmployeeCheckFilterUIInterface) => {
            dispatch(cancelChecksPeriodAction(selectedEmployee, filter));
        }
    }
};

const connector = connect(mapStateToProps, mapDispatchToProps);

type PropsFromRedux = ConnectedProps<typeof connector>;

export default connector(Container);

