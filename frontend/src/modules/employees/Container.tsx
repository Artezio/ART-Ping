import React, {useEffect, useMemo, useState} from 'react';
import {connect, ConnectedProps} from "react-redux";
import {addEmployee, deleteEmployeeAction, deleteGroupEmployeesAction, editEmployeeAction,} from "./actions";
import {EmployeeFilterUIInterface, EmployeeInterface} from "./types";
import {AppRootState, TypedSelector} from '../../store';
import {getEmployees, getTotal, isEmployeesLoading} from "./selectors";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import {EditorMode} from "../../store/admin/types";
import {
    Backdrop,
    Button,
    Checkbox,
    CircularProgress,
    FormControlLabel,
    FormGroup,
    IconButton,
    Menu,
    MenuItem,
    Toolbar
} from "@material-ui/core";
import {DataGrid} from "@material-ui/data-grid";
import {DialogConfirm} from "../../common/dialogs/DialogConfirm";
import {AddUserDialog as AddEmployeesDialog} from "./components/AddEmployeesDialog";
import {EditEmployeesDialog} from "./components/EditEmployeesDialog";
import {reloadEmployees} from "../employees/actions";
import PopupState, {bindMenu, bindTrigger} from 'material-ui-popup-state';
import MoreVert from '@material-ui/icons/MoreVert';
import SearchIcon from '@material-ui/icons/Search';
import {SearchTextInput} from '../employeesCheck/components';
import Dropdown from '../../components/misc/Dropdown/Dropdown';
import AddIcon from "@material-ui/icons/Add";
import {buttonDisabledStyle, buttonStyle} from '../../common/styles';
import {GetCurrentUser} from "../../store/user/actions";
import {hasAccessAddEditEmployeeSelector} from "../employeesCheck/selectors";
import CustomPagination from "../../common/customPagination/CustomPagination";
import {CheckedIconTrue} from "../employeesCheck/components/IndicateCheck";
import {CheckedIconFalse} from "../employeesCheck/components/IndicateCheck";


interface Props extends PropsFromRedux {
    hideToolbar?: boolean;
    checkboxSelection?: boolean;
    disableSelectionOnClick?: boolean;
    getEmployee: any;
    readOnly?: boolean,
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
        toolbar: any,
        listFilters: any,
        dataGrid: any,
        actionColumn: any,
        actionButton: any,
        dialogEdit: any,
        dialogConfirm: any,
        backdrop: any,
        dropdown: any,
        searchButton: any,
    } =>
        createStyles({
            root: {
                height: "calc(100% - 214px)",
                width: '100%',
            },

            toolbar: {},
            listFilters: {},
            searchButton: {color: 'black'},
            dataGrid: {
                backgroundColor: "#FFFFFF",
            },
            dropdown: {
                flexGrow: 1,
                display: "flex",
                marginTop: "-16px"
            },
            actionColumn: {
                backgroundColor: "#000000",
            },
            actionButton: {},
            dialogEdit: {},
            dialogConfirm: {},
            backdrop: {
                zIndex: theme.zIndex.drawer + 1,
                color: '#fff',
            },
        })
);

const Container = ({
                       employees = [],
                       roles = [],
                       total = 0,
                       isEmployeesLoading = false,
                       onAddEmployee,
                       getEmployee,
                       onEditEmployee,
                       onDeleteEmployee,
                       getRoles,
                       onFilterEmployees,
                       onDeleteGroupEmployees,
                       hideToolbar,
                       hasAccessAddEditEmployee,
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


    useEffect(() => {
        if (getEmployee)
            getEmployee();
        if (getRoles)
            getRoles();
    }, [getEmployee, getRoles]);

    useEffect(() => {
        handlers.searchSubmit();
    }, [filtersState.sort]);
    const classes = useStyles();
    const {t} = useTranslation();

    const [selectionModel, setSelectionModel] = useState<any[]>([]);

    const [isIsAddDialogOpen, setIsAddDialogOpen] = useState<boolean>(false);
    const [IsEditDialogOpen, setIsEditDialogOpen] = useState<boolean>(false);

    const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
    const [confirmationData, setConfirmationData] = useState<any>({mode: "", data: {}});

    const references = TypedSelector((state: any) => {
        return state.references.refs;
    });

    const offices = TypedSelector(state => state.references).refs['offices']?.list || [];
    const projects = TypedSelector(state => state.references).refs['projects']?.list || [];
    const calendars = TypedSelector(state => state.references).refs['calendars']?.list || [];

    const [editorFormMode, setEditorFormMode] = useState<string>(EditorMode.CREATE);
    const [editedData, setEditedData] = useState<any>({});

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
            return {...item, roles: rolesArray, projects: projectsArray, offices: officesArray}
        });
        return employeesList
    }, [employees, projects, offices]);
    const columns = useMemo(() => {

        return [
            {
                field: 'Active',
                headerName: t("Active"),
                width: 117,
                align: 'center' as const,
                renderCell: ({row: {active}}: any) => {
                    if (active === true) {
                        return <CheckedIconTrue/>;
                    }
                    return <CheckedIconFalse/>;
                }
            },
            {
                field: 'name',
                headerName: t('Name'),
                flex: 1,
                renderCell: ({row: {lastName, firstName}}: any): string => {
                    return `${lastName} ${firstName} `;
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
                renderCell: ({row: {email}}: any): string => {
                    return `${email} `;
                },
            },
            {
                field: 'roleNames',
                flex: 1,
                headerName: t('Role'),
                renderCell: ({row: {roles}}: any) => {
                    if (!roles || !roles.length)
                        return "";
                    return roles.reduce((a: string, b: string) => a + ", " + b);
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
                    return projects.reduce((a: string, b: string) => (!a ? "" : a + ", ") + (b ?? ""));
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
                        return references['offices'].list.find(({id}: any) => id === baseOffice)?.name;
                    }
                    return "";
                },
                info: {
                    type: "select",
                    options: offices
                }
            },
            {
                className: "actionColumn",
                field: "",
                sortable: false,
                disableClickEventBubbling: true,
                width: 80,
                disableColumnMenu: true,
                renderCell: (params: any) => {
                    return <>
                        <PopupState variant="popover" popupId="demo-popup-menu">
                            {(popupState) => (
                                <React.Fragment>
                                    <IconButton disabled={!hasAccessAddEditEmployee} {...bindTrigger(popupState)}>
                                        <MoreVert fontSize="inherit"/>
                                    </IconButton>
                                    <Menu {...bindMenu(popupState)}>
                                        <MenuItem onClick={() => {
                                            handlers.edit(params.row);
                                            popupState.close();
                                        }}>{params.row.active ? t('Edit') : t('View')}</MenuItem>
                                        {params.row.active && <MenuItem onClick={() => {
                                            handlers.delete(params.row);
                                            popupState.close();
                                        }}>{t('Delete')}</MenuItem>}
                                    </Menu>
                                </React.Fragment>
                            )}
                        </PopupState>
                    </>;
                }
            }
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
        onlyActiveEmployees: ({target: {checked}}: any) => {
            setFiltersState({
                ...filtersState,
                onlyActiveEmployees: checked
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
        add: () => {

            setIsAddDialogOpen(true);
            setEditedData({});
        },
        edit: (data: any) => {
            setIsEditDialogOpen(true);
            setEditedData(data);
        },

        delete: (data: any) => {

            setIsConfirmOpen(true);
            setConfirmationData({mode: "single", data});
        },

        deleteGroup: () => {
            setIsConfirmOpen(true);
            setConfirmationData({mode: "multi", data: selectionModel});
        },
        handlePageChange: (params: any) => {
            let fs = {...filtersState, pageNumber: params};
            setFiltersState(fs);
            onFilterEmployees(fs);
        },
        addFormSubmit: (formData: any) => {
            onAddEmployee(formData, setIsAddDialogOpen, filtersState);
        },

        editFormSubmit: (formData: any, updatedCallback: any) => {
            onEditEmployee(formData, updatedCallback, filtersState);
            setIsEditDialogOpen(false);
        },
        deleteConfirm: () => {
            switch (confirmationData?.mode) {
                case "single":
                    onDeleteEmployee(confirmationData.data, filtersState);
                    break;
                case "multi":
                    onDeleteGroupEmployees(confirmationData.data);
                    break;
                default:
                    break;
            }
        },
        handlePageSizeChange: (itemsOnPage: number) => {
            let fs = {...filtersState, pageSize: itemsOnPage, pageNumber: 0};
            setFiltersState(fs);
            onFilterEmployees(fs);
        }
    }


    return <div className={classes.root}>
        {<Toolbar className={classes.listFilters}>
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
            <FormGroup>
                <FormControlLabel control={<Checkbox
                    checked={filtersState?.onlyActiveEmployees || false}
                    onChange={handlers.onlyActiveEmployees}
                />} label={t("Active")}/>
            </FormGroup>

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

        {!hideToolbar && <Toolbar>
            <Button
                disabled={!hasAccessAddEditEmployee}
                style={hasAccessAddEditEmployee ? buttonStyle : buttonDisabledStyle}
                startIcon={<AddIcon/>}
                color="secondary"
                variant="contained"
                onClick={handlers.add}
            >{t('Add')}</Button>
        </Toolbar>}

        <DataGrid
            className={classes.dataGrid}
            sortingMode="server"
            onSortModelChange={handlers.handleSortModelChange}
            disableColumnMenu
            rows={preparedEmployeesList}
            columns={columns}
            pageSize={filtersState.pageSize}
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

        <DialogConfirm
            open={isConfirmOpen}
            onYesClick={() => {
                setIsConfirmOpen(false);
                handlers.deleteConfirm();
            }}
            onNoClick={() => {
                setIsConfirmOpen(false)
            }}/>

        <AddEmployeesDialog
            employees={employees}
            calendars={calendars}
            offices={offices}
            projects={projects}
            editedData={editedData}
            open={isIsAddDialogOpen}
            mode={editorFormMode}
            onCancel={() => {
                setIsAddDialogOpen(false)
            }}
            formSubmit={handlers.addFormSubmit}/>

        <EditEmployeesDialog
            offices={offices}
            calendars={calendars}
            editedData={editedData}
            open={IsEditDialogOpen}
            mode={editorFormMode}
            onCancel={() => {
                setIsEditDialogOpen(false)
            }}
            formSubmit={handlers.editFormSubmit}/>

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
            dispatch(reloadEmployees(filter));
        },
        onAddEmployee: (employee: EmployeeInterface, setIsAddDialogOpen: any, filter: EmployeeFilterUIInterface) => {
            dispatch(addEmployee(employee, setIsAddDialogOpen, filter));
        },
        getEmployee: () => {
            dispatch(reloadEmployees());
        },
        onDeleteEmployee: (employee: EmployeeInterface, filtersState: EmployeeFilterUIInterface) => {
            dispatch(deleteEmployeeAction(employee, filtersState));
        },
        onEditEmployee: (employee: EmployeeInterface, updatedCallback: any, filtersState: EmployeeFilterUIInterface) => {
            dispatch(editEmployeeAction(employee, updatedCallback, filtersState));
        },
        getRoles: () => {
            dispatch(GetCurrentUser())
        },
        onDeleteGroupEmployees: (deletedGroup: Array<EmployeeInterface>) => {
            dispatch(deleteGroupEmployeesAction(deletedGroup))
        }
    };
};

const connector = connect(mapStateToProps, mapDispatchToProps);

type PropsFromRedux = ConnectedProps<typeof connector>;

export default connector(Container);
