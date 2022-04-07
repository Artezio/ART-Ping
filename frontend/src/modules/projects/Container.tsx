import React, {useEffect, useMemo, useState} from 'react';
import {connect, ConnectedProps} from "react-redux";
import {addProject, deleteGroupProjectsAction, deleteProjectAction, editProjectAction, reloadProjects} from "./actions";
import {ProjectFilterUIInterface, ProjectInterface} from "./types";
import {getProjects, getTotal, isProjectsLoading} from "./selectors";
import {AppRootState, TypedSelector} from "../../store";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import {Backdrop, Button, CircularProgress, IconButton, Menu, MenuItem, Toolbar} from "@material-ui/core";
import {DataGrid} from "@material-ui/data-grid";
import {DialogConfirm} from "../../common/dialogs/DialogConfirm";
import {EditProjectDialog} from "./components/EditProjectDialog";
import {AddProjectDialog} from "./components/AddProjectDialog";
import {getEmployees} from "../employees/selectors";
import PopupState, {bindMenu, bindTrigger} from 'material-ui-popup-state';
import MoreVert from '@material-ui/icons/MoreVert';
import SearchIcon from '@material-ui/icons/Search';
import {SearchTextInput} from '../employeesCheck/components';
import AddIcon from "@material-ui/icons/Add";
import {buttonStyle} from '../../common/styles';
import {
    hasAccessAddProjectsSelector,
    hasAccessEditProjectsSelector,
    hasAccessProjectsSelector
} from "../employeesCheck/selectors";
import CustomPagination from "../../common/customPagination/CustomPagination";
import {CalendarFilterUIInterface} from "../calendars";
import {HR} from "../../store/user/constants";

interface Props extends PropsFromRedux {
    hideToolbar?: boolean;
    checkboxSelection?: boolean;
    disableSelectionOnClick?: boolean;
    getEmployees?: any;
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
        toolbar: any,
        dataGrid: any,
        listFilters: any,
        actionColumn: any,
        actionButton: any,
        dialogEdit: any,
        dialogConfirm: any,
        backdrop: any,
        searchButton: any,
    } =>
        createStyles({
            root: {
                height: "calc(100% - 214px)",
                width: '100%',
            },
            listFilters: {},
            searchButton: {color: 'black'},
            toolbar: {},
            dataGrid: {
                backgroundColor: "#FFFFFF",
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
                       projects = [],
                       total = 0,
                       hideToolbar,
                       checkboxSelection = true,
                       disableSelectionOnClick = true,
                       isProjectsLoading,
                       onAddProject,
                       onDeleteGroupProjects,
                       onDeleteProject,
                       onEditProject,
                       getEmployees,
                       getProjects,
                       onFilterProjects,
                       hasAccessProject,
                       hasAccessEditProjects,
                       hasAccessAddProjects,
                   }: Props) => {
    useEffect(() => {
        if (getProjects)
            getProjects(filtersState);
        if (getEmployees)
            getEmployees();
    }, [getProjects, getEmployees])
    const defaultFilterState: ProjectFilterUIInterface = {
        searchString: "",
        pageSize: 20,
        pageNumber: 0,
        sort: [],
    };
    const currentUser = TypedSelector(state => state.user);
    const classes = useStyles();
    const {t} = useTranslation();
    const [filtersState, setFiltersState] = React.useState<ProjectFilterUIInterface>(defaultFilterState);
    const [selectionModel, setSelectionModel] = useState<any[]>([]);
    const [isIsAddDialogOpen, setIsAddDialogOpen] = useState<boolean>(false);
    const [IsEditDialogOpen, setIsEditDialogOpen] = useState<boolean>(false);
    const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
    const [confirmationData, setConfirmationData] = useState<any>({mode: "", data: {}});
    const [editedData, setEditedData] = useState<any>({});
    useEffect(() => {
        handlers.searchSubmit();
    }, [filtersState.sort]);
    const columns = useMemo(() => {
        return [
            {
                field: 'id',
                headerName: 'ID',
                width: 500,
                hide: true
            },
            {
                field: 'name',
                headerName: t('Project name'),
                flex: 1,
                renderCell: ({row: {name}}: any): string => {
                    return `${name} `;
                },
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
                                    <IconButton {...bindTrigger(popupState)}>
                                        <MoreVert fontSize="inherit"/>
                                    </IconButton>
                                    <Menu {...bindMenu(popupState)}>
                                        <MenuItem onClick={() => {
                                            handlers.edit(params.row);
                                            popupState.close();
                                        }}>{t('Edit')}</MenuItem>
                                        <MenuItem onClick={() => {
                                            handlers.delete(params.row);
                                            popupState.close();
                                        }}>{t('Delete')}</MenuItem>
                                    </Menu>
                                </React.Fragment>
                            )}
                        </PopupState>
                    </>;
                }
            }
        ];
    }, []);
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
        searchSubmit: () => {
            let fs = {...filtersState, pageNumber: 0};
            setFiltersState(fs);
            onFilterProjects(fs);
        },
        filtersReset: () => {
            const filter = {...defaultFilterState};
            setFiltersState(filter);
            onFilterProjects(filter);
        },
        add: () => {
            const defaultData: { name: string, pmIds: string[], employeeIds: string[] } = {
                name: "",
                pmIds: [],
                employeeIds: []
            };
            if (currentUser.roles.indexOf(HR) !== -1 && currentUser) {
                defaultData.pmIds.push(currentUser?.id);
                defaultData.employeeIds.push(currentUser?.id);
            }
            setIsAddDialogOpen(true);
            setEditedData(defaultData);
        },
        edit: (data: any) => {
            setIsEditDialogOpen(true);
            setEditedData(data);
        },

        delete: (data: any) => {
            setIsConfirmOpen(true);
            setConfirmationData({mode: "single", data});
        },
        handlePageChange: (params: any) => {
            let fs = {...filtersState, pageNumber: params};
            setFiltersState(fs);
            onFilterProjects(fs);
        },
        deleteGroup: () => {
            setIsConfirmOpen(true);
            setConfirmationData({mode: "multi", data: selectionModel});
        },


        addFormSubmit: (formData: any) => {
            onAddProject(formData, filtersState);
            setIsAddDialogOpen(false);
        },

        editFormSubmit: (formData: any, updatedCallback: any) => {
            onEditProject(formData, updatedCallback, filtersState);
            setIsEditDialogOpen(false);
        },
        deleteConfirm: () => {
            switch (confirmationData?.mode) {
                case "single":
                    onDeleteProject(confirmationData.data, filtersState);
                    break;
                case "multi":
                    onDeleteGroupProjects(confirmationData.data);
                    break;
                default:
                    break;
            }
        },
        handlePageSizeChange: (itemsOnPage: number) => {
            let fs = {...filtersState, pageSize: itemsOnPage, pageNumber: 0};
            setFiltersState(fs);
            onFilterProjects(fs);
        }
    }
    return <div className={classes.root}>
        {<Toolbar className={classes.listFilters}>
            <SearchTextInput
                value={filtersState?.searchString || ""}
                onChange={handlers.searchString}
            />

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
                style={buttonStyle}
                startIcon={<AddIcon/>}
                color="secondary"
                variant="contained"
                onClick={handlers.add}
            >{t('Add')}</Button>
            {/*<Button*/}
            {/*    disabled={!hasAccessProject}*/}
            {/*    style={buttonStyle}*/}
            {/*    variant="outlined"*/}
            {/*    onClick={handlers.deleteGroup}*/}
            {/*>{t('Delete')}</Button>*/}
        </Toolbar>}
        <DataGrid
            className={classes.dataGrid}
            sortingMode="server"
            onSortModelChange={handlers.handleSortModelChange}
            disableColumnMenu
            rows={projects}
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

        <AddProjectDialog
            editedData={editedData}
            open={isIsAddDialogOpen}
            employees={employees}
            onCancel={() => {
                setIsAddDialogOpen(false)
            }}
            formSubmit={handlers.addFormSubmit}/>

        <EditProjectDialog
            editedData={editedData}
            isOnlyView={!hasAccessEditProjects}
            open={IsEditDialogOpen}
            onCancel={() => {
                setIsEditDialogOpen(false)
            }}
            formSubmit={handlers.editFormSubmit}/>

        <Backdrop
            className={classes.backdrop}
            open={isProjectsLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </div>
}

const mapStateToProps = (state: AppRootState) => ({
    employees: getEmployees(state),
    total: getTotal(state),
    projects: getProjects(state),
    isProjectsLoading: isProjectsLoading(state),
    hasAccessProject: hasAccessProjectsSelector(state),
    hasAccessEditProjects: hasAccessEditProjectsSelector(state),
    hasAccessAddProjects: hasAccessAddProjectsSelector(state)
})
const mapDispatchToProps = (dispatch: any) => {
    return {
        onFilterProjects: (filter: ProjectFilterUIInterface) => {
            dispatch(reloadProjects(filter));
        },
        onAddProject: (projects: ProjectInterface, filter: CalendarFilterUIInterface) => {
            dispatch(addProject(projects, filter));
        },
        getProjects: (filter: ProjectFilterUIInterface) => {
            dispatch(reloadProjects(filter));
        },
        onDeleteProject: (projects: ProjectInterface, filtersState: ProjectFilterUIInterface) => {
            dispatch(deleteProjectAction(projects, filtersState));
        },
        onEditProject: (projects: ProjectInterface, updatedCallback: any, filtersState: ProjectFilterUIInterface) => {
            dispatch(editProjectAction(projects, updatedCallback, filtersState));
        },
        onDeleteGroupProjects: (projects: Array<ProjectInterface>) => {
            dispatch(deleteGroupProjectsAction(projects))
        },
    };
};

const connector = connect(mapStateToProps, mapDispatchToProps)

type PropsFromRedux = ConnectedProps<typeof connector>

export default connector(Container)
