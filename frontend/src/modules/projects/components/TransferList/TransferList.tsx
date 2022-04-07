import React, {useEffect, useMemo, useState} from 'react';
import {useFormikContext} from "formik";
import Grid from '@material-ui/core/Grid';
import ListPanel from './ListPanel';
import DividerPanel from './DividerPanel';
import * as helpers from './helpers';
import {Backdrop, Button, CircularProgress, Toolbar, Tooltip} from "@material-ui/core";
import {connect, ConnectedProps} from "react-redux";
import {EmployeeFilterUIInterface} from "../../../employees/types";
import {AppRootState, TypedSelector} from '../../../../store';
import {getEmployees, getTotal, isEmployeesLoading} from "../../../employees/selectors";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import {DataGrid} from "@material-ui/data-grid";
import {reloadEmployees} from "../../../employees/actions";
import SearchIcon from '@material-ui/icons/Search';
import {SearchTextInput} from '../../../employeesCheck/components';
import Dropdown from '../../../../components/misc/Dropdown/Dropdown';
import {buttonStyle} from '../../../../common/styles';
import {GetCurrentUser} from "../../../../store/user/actions";
import CustomPagination from "../../../../common/customPagination/CustomPagination";
import {hasAccessAddEditEmployeeSelector} from "../../../employeesCheck/selectors";

interface Props extends PropsFromRedux {
    hideToolbar?: boolean;
    checkboxSelection?: boolean;
    disableSelectionOnClick?: boolean;
    getEmployee: any;
    name: string,
    addUsersInWork: (users: any[]) => void,
    available?: any[],
    selected?: any[]
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
        dataGrid: any,
        backdrop: any,
        dropdown: any,
        searchButton: any,
        transferList: any,
        dividerPanel: any,
        listPanel: any
    } =>
        createStyles({
            root: {
                height: "500px",
                width: '100%',
                "& .MuiDataGrid-root": {
                    fontSize: "12px"
                },
                "& .MuiButton-textSecondary": {
                    color: "#f44336",
                },
                "& .MuiCheckbox-colorSecondary.Mui-checked": {
                    color: "#f44336",
                }
            },
            searchButton: {color: 'black'},
            dataGrid: {
                backgroundColor: "#ffffff",
                "& .already-in-project": {
                    backgroundColor: "#f1f1f1",
                    pointerEvents: "none",
                    color: "#adadad"
                }
            },
            dropdown: {
                flexGrow: 1,
                display: "flex",
                marginTop: "-16px"
            },
            backdrop: {
                zIndex: theme.zIndex.drawer + 1,
                color: '#fff',
            },
            transferList: {
                display: "flex",
                height: "calc(100% - 64px)"
            },
            dividerPanel: {
                width: 80,
                margin: "100px 20px 0px 20px",

            },
            listPanel: {
                width: "200px",
                height: "100%"
            },

        })
);

const TransferList = ({
                          employees = [],
                          roles = [],
                          total = 0,
                          isEmployeesLoading = false,
                          getEmployee,
                          getRoles,
                          onFilterEmployees,
                          checkboxSelection = true,
                          disableSelectionOnClick = true,
                          name,
                          addUsersInWork,
                          selected
                      }: Props) => {

    const defaultFilterState: EmployeeFilterUIInterface = {
        officeId: null,
        projectId: null,
        searchString: "",
        role: null,
        pageNumber: 0,
        pageSize: 20,
        onlyActiveEmployees: true,
        sort: [],
    };


    const [filtersState, setFiltersState] = React.useState<EmployeeFilterUIInterface>(defaultFilterState);
    const {setFieldValue} = useFormikContext<any>();
    const [checked, setChecked] = React.useState<number[]>([]);
    const [left, setLeft] = React.useState<any[]>([]);
    const [right, setRight] = React.useState<any[]>([]);
    const leftChecked = helpers.intersection(checked, left);
    const rightChecked = helpers.intersection(checked, right);

    const beforeSetRight = (rValues: any[]) => {
        if (name) {
            setFieldValue(name, rValues.map((item: any) => item.id));
        }
        addUsersInWork(rValues);
        setRight(rValues);
    }
    useEffect(() => {
        let availableList: any[] = [];
        employees.forEach((item: any) => {
            availableList.push({
                id: item.id,
                name: `${item.lastName} ${item.firstName}`,
                description: item.login
            })
        })
        setLeft(availableList);
    }, [employees]);
    useEffect(() => {
        if (getEmployee)
            getEmployee();
        if (getRoles)
            getRoles();
    }, [getEmployee, getRoles]);
    useEffect(() => {
        setRight(selected || []);
        if (name) {
            setFieldValue(name, selected?.map((item: any) => item.id));
        }
    }, []);
    useEffect(() => {
        handlers.searchSubmit();
    }, [filtersState.sort]);
    const classes = useStyles();
    const {t} = useTranslation();
    const [selectionModel, setSelectionModel] = useState<any[]>([]);

    const references = TypedSelector((state: any) => {
        return state.references.refs;
    });
    const offices = TypedSelector(state => state.references).refs['offices']?.list || [];
    const projects = TypedSelector(state => state.references).refs['projects']?.list || [];
    const preparedEmployeesList = useMemo(() => {
        const ascSort = (arrayToSort: any) => {
            arrayToSort.sort(
                function (a: string, b: string) {
                    if (a.toLowerCase() < b.toLowerCase()) return -1;
                    if (a.toLowerCase() > b.toLowerCase()) return 1;
                    return 0;
                }
            );
        }
        const descSort = (arrayToSort: any) => {
            arrayToSort.sort(
                function (a: string, b: string) {
                    if (a.toLowerCase() < b.toLowerCase()) return 1;
                    if (a.toLowerCase() > b.toLowerCase()) return -1;
                    return 0;
                }
            );
        }

        const employeesList = employees.map((item: any) => {
            let status = '';
            right.forEach((employee) => {
                if (employee.id === item.id) {
                    status = "already-in-project"
                }
            });
            let rolesArray = item?.roles.map((role: string) => t(role));
            let projectsArray = item?.projects.map((a: string) => projects.find(({id}: any) => id === a)?.name);
            let officesArray = item?.offices.map((a: string) => offices.find(({id}: any) => id === a)?.name);
            if (filtersState?.sort.length > 0) {
                filtersState?.sort.forEach((item: any) => {
                    if (item['field'] === 'roleNames') {
                        item['sort'] === 'asc' ? ascSort(rolesArray) : descSort(rolesArray);
                    }
                    if (item['field'] === 'projects') {
                        item['sort'] === 'asc' ? ascSort(projectsArray) : descSort(projectsArray);
                    }
                    if (item['field'] === 'offices') {
                        item['sort'] === 'asc' ? ascSort(officesArray) : descSort(officesArray);
                    }
                })
            }
            return {...item, roles: rolesArray, projects: projectsArray, offices: officesArray, status}
        });
        return employeesList
    }, [employees, projects, offices, right]);

    const columns = useMemo(() => {
        return [
            {
                field: 'name',
                headerName: t('Name'),
                flex: 1,
                renderCell: ({row: {lastName, firstName}}: any) => {
                    return <Tooltip
                        title={`${lastName} ${firstName}`}
                        placement="top-start">
                        <div>{`${lastName} ${firstName}`}</div>
                    </Tooltip>
                },
            },
            {
                field: 'login',
                headerName: t('Login'),
                sortable: false,
                flex: 1,
                hide: true,
            },
            {
                field: 'email',
                flex: 1,
                headerName: t('Email'),
                renderCell: ({row: {email}}: any) => {
                    return <Tooltip
                        title={email}
                        placement="top-start">
                        <div>{email}</div>
                    </Tooltip>
                },
            },
            {
                field: 'roleNames',
                flex: 1,
                headerName: t('Role'),
                renderCell: ({row: {roles}}: any) => {
                    if (!roles || !roles.length)
                        return "";
                    const rolesString = roles.reduce((a: string, b: string) => a + ", " + b);
                    return <Tooltip
                        title={rolesString}
                        placement="top-start">
                        <div>{rolesString}</div>
                    </Tooltip>
                }
            },
            {
                field: 'projects',
                headerName: t('Project'),
                flex: 1,
                renderCell: ({row: {projects}}: any) => {
                    if (!projects || !projects.length) {
                        return "";
                    }
                    const projectsString = projects.reduce((a: string, b: string) => (!a ? "" : a + ", ") + (b ?? ""));
                    return <Tooltip
                        title={projectsString}
                        placement="top-start">
                        <div>{projectsString}</div>
                    </Tooltip>
                },
                info: {
                    type: "select",
                    options: projects
                }
            },
            {
                field: 'baseOffice',
                headerName: t('Office'),
                flex: 1,
                renderCell: ({row: {baseOffice}}: any) => {
                    if (!baseOffice || baseOffice === "") {
                        return "";
                    }
                    if (references['offices']) {
                        const officesString = references['offices'].list.find(({id}: any) => id === baseOffice)?.name;
                        return <Tooltip
                            title={officesString}
                            placement="top-start">
                            <div>{officesString}</div>
                        </Tooltip>
                    }
                    return "";
                },
                info: {
                    type: "select",
                    options: offices
                }
            },
        ];
    }, [offices]);

    const handlers = {
        handleSortModelChange: (newModel: any) => {
            setFiltersState({
                ...filtersState,
                sort: newModel
            });
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
        role: (value: any) => {
            setFiltersState({
                ...filtersState,
                role: value
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
            onFilterEmployees(fs);
        },
        filtersReset: () => {
            const filter = {...defaultFilterState};
            setFiltersState(filter);
            onFilterEmployees(filter);
        },
        select: (newSelection: any) => {
            let checkedLeft: any[] = [];
            setSelectionModel(newSelection);
            newSelection?.forEach((value: string) => {
                let index = left.findIndex((item: any) => item.id === value);
                if (index > -1) {
                    checkedLeft.push(left[index]);
                }
            })
            setChecked(checkedLeft)
        },
        handlePageChange: (params: any) => {
            let fs = {...filtersState, pageNumber: params};
            setFiltersState(fs);
            onFilterEmployees(fs);
        },
        handlePageSizeChange: (itemsOnPage: number) => {
            let fs = {...filtersState, pageSize: itemsOnPage, pageNumber: 0};
            setFiltersState(fs);
            onFilterEmployees(fs);
        }
    }


    return <div className={classes.root}>
        {<Toolbar>
            <SearchTextInput
                value={filtersState?.searchString || ""}
                onChange={handlers.searchString}
            />
            <div className={classes.dropdown}>
                <Dropdown
                    label={t("Roles")}
                    defaultValue={defaultFilterState?.role || null}
                    value={filtersState?.role || null}
                    values={roles}
                    onChange={handlers.role}
                />
            </div>
            <div className={classes.dropdown}>
                <Dropdown
                    label={t("Offices")}
                    defaultValue={defaultFilterState?.officeId || null}
                    value={filtersState?.officeId || null}
                    values={references['offices']?.list ?? []}
                    onChange={handlers.office}
                />
            </div>
            <div className={classes.dropdown}>
                <Dropdown
                    label={t("Projects")}
                    defaultValue={defaultFilterState?.projectId || null}
                    values={references['projects']?.list ?? []}
                    value={filtersState?.projectId || null}
                    onChange={handlers.project}
                />
            </div>
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
        <div className={classes.transferList}>
            <DataGrid
                className={classes.dataGrid}
                sortingMode="server"
                onSortModelChange={handlers.handleSortModelChange}
                disableColumnMenu
                rows={preparedEmployeesList}
                //@ts-ignore
                getRowClassName={(params) => `${params.row.status}`}
                columns={columns}
                pageSize={filtersState.pageSize}
                checkboxSelection={checkboxSelection}
                disableSelectionOnClick={disableSelectionOnClick}
                onSelectionModelChange={handlers.select}
                selectionModel={selectionModel}
                paginationMode="server"
                rowsPerPageOptions={[20]}
                rowCount={total}
                components={{
                    Pagination: CustomPagination,
                }}
                componentsProps={{
                    pagination: {
                        total: total,
                        onPageChange: handlers.handlePageChange,
                        count: Math.ceil(total / filtersState.pageSize),
                        page: filtersState.pageNumber,
                        itemsOnPage: filtersState.pageSize,
                        onItemsOnPageChange: handlers.handlePageSizeChange
                    }
                }}
            />
            <Grid item className={classes.dividerPanel}><DividerPanel {...{
                left,
                setLeft,
                leftChecked,
                right,
                setRight: beforeSetRight,
                rightChecked,
                setChecked,
                setSelectionModel
            }} /></Grid>
            <Grid item className={classes.listPanel}><ListPanel {...{
                title: t('Employees in project'),
                items: right,
                checked,
                setChecked,
                setSelectionModel
            }} /></Grid>
        </div>

        <Backdrop
            className={classes.backdrop}
            open={isEmployeesLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </div>
}


const mapStateToProps = (state: AppRootState) => {
    return ({
        employees: getEmployees(state),
        isEmployeesLoading: isEmployeesLoading(state),

        roles: [
            {
                id: "Администратор",
                name: "Администратор",
                value: "Администратор",
            },
            {
                id: "Менеджер по персоналу",
                name: "Менеджер по персоналу",
                value: "Менеджер по персоналу",
            },
            {
                id: "Руководитель офиса",
                name: "Руководитель офиса",
                value: "Руководитель офиса",
            },
            {
                id: "Руководитель проекта",
                name: "Руководитель проекта",
                value: "Руководитель проекта",
            },
            {
                id: "Сотрудник",
                name: "Сотрудник",
                value: "Сотрудник",
            }
        ],
        total: getTotal(state),
        hasAccessAddEditEmployee: hasAccessAddEditEmployeeSelector(state)

    });
}


const mapDispatchToProps = (dispatch: any) => {
    return {
        onFilterEmployees: (filter: EmployeeFilterUIInterface) => {
            dispatch(reloadEmployees(filter, 'projects'));
        },
        getEmployee: () => {
            dispatch(reloadEmployees(null, 'projects'));
        },
        getRoles: () => {
            dispatch(GetCurrentUser())
        },
    };
};

const connector = connect(mapStateToProps, mapDispatchToProps);

type PropsFromRedux = ConnectedProps<typeof connector>;

export default connector(TransferList);
