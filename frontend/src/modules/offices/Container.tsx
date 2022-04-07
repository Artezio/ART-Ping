import React, {useMemo, useEffect, useState} from 'react';
import {connect, ConnectedProps, useSelector} from "react-redux";
import {addOffice, deleteGroupOfficesAction, deleteOfficeAction, editOfficeAction, reloadOffices} from "./actions";
import {OfficeFilterUIInterface, OfficeInterface} from "./types";
import {getOffices, getTotal, isOfficesLoading} from "./selectors";
import {AppRootState, TypedSelector} from "../../store";
import {Backdrop, Button, CircularProgress, IconButton, Menu, MenuItem, Toolbar} from "@material-ui/core";
import {DataGrid} from "@material-ui/data-grid";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import {EditorMode} from "../../store/admin/types";
import {DialogConfirm} from "../../common/dialogs/DialogConfirm";
import {AddOfficeDialog} from "./components/AddOfficeDialog";
import {EditOfficeDialog} from "./components/EditOfficeDialog";
import PopupState, {bindMenu, bindTrigger} from 'material-ui-popup-state';
import MoreVert from '@material-ui/icons/MoreVert';
import SearchIcon from '@material-ui/icons/Search';
import {SearchTextInput} from '../employeesCheck/components';
import AddIcon from "@material-ui/icons/Add";
import {buttonDisabledStyle, buttonStyle} from "../../common/styles";
import moment from "moment-timezone/moment-timezone-utils";
import {hasAccessOfficesSelector, hasAccessAddOfficesSelector, hasAccessEditOfficesSelector} from "../employeesCheck/selectors";
import CustomPagination from "../../common/customPagination/CustomPagination";
import {CalendarFilterUIInterface} from "../calendars";

interface Props extends PropsFromRedux {
    hideToolbar?: boolean;
    checkboxSelection?: boolean,
    disableSelectionOnClick?: boolean,

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

const PageOffices = ({
                         offices = [],
                         isOfficesLoading,
                         total = 0,
                         hideToolbar,
                         onAddOffice,
                         onEditOffice,
                         onDeleteGroupOffices,
                         onDeleteOffice,
                         getOffices,
                         onFilterOffices,
                         hasAccessOffice,
                         hasAccessAddOffice,
                         hasAccessEditOffice,
                     }: Props) => {

    useEffect(() => {
        if (getOffices)
            getOffices(filtersState);
    }, [getOffices])

    const defaultFilterState: OfficeFilterUIInterface = {
        searchString: "",
        pageSize: 20,
        pageNumber: 0,
        sort: [],
    };

    const classes = useStyles();
    const {t} = useTranslation();

    const [filtersState, setFiltersState] = React.useState<OfficeFilterUIInterface>(defaultFilterState);
    const [selectionModel, setSelectionModel] = useState<any[]>([]);
    const [isIsAddDialogOpen, setIsAddDialogOpen] = useState<boolean>(false);
    const [IsEditDialogOpen, setIsEditDialogOpen] = useState<boolean>(false);
    const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
    const [confirmationData, setConfirmationData] = useState<any>({mode: "", data: {}});
    const [editorFormMode] = useState<string>(EditorMode.CREATE); // create|edit
    const [editedData, setEditedData] = useState<any>({}); // create|edit
    useEffect(() => {
        handlers.searchSubmit();
    }, [filtersState.sort]);

    const references = TypedSelector((state: any) => {
        return state.references.refs;
    });

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
                flex: 1,
                headerName: t('Office name'),
                renderCell: ({row: {name}}: any): string => {
                    return `${name} `;
                },
            },
            {
                field: 'calendarName',
                flex: 1,
                headerName: t('Calendar'),
                renderCell: ({row: {calendarId}}: any) => {
                    if (references['calendars']) {
                        return references['calendars'].list.find(({id}: any) => id === calendarId)?.name;
                    }
                    return "";
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
                                    <IconButton disabled={!hasAccessOffice}{...bindTrigger(popupState)}>
                                        <MoreVert fontSize="inherit"/>
                                    </IconButton>
                                    <Menu {...bindMenu(popupState)}>
                                        <MenuItem onClick={() => {
                                            handlers.edit(params.row);
                                            popupState.close();
                                        }}>{hasAccessEditOffice ? t('Edit') : t('View')}</MenuItem>
                                        {hasAccessEditOffice && <MenuItem onClick={() => {
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
            onFilterOffices(fs);
        },
        filtersReset: () => {
            const filter = {...defaultFilterState};
            setFiltersState(filter);
            onFilterOffices(filter);
        },
        add: () => {
            setIsAddDialogOpen(true);
            setEditedData({name: "", calendarId: "", timezone: moment.tz.guess()});
        },
        edit: (data: any) => {
            setIsEditDialogOpen(true);
            setEditedData(data);
        },
        handlePageChange: (params: any) => {
            let fs = {...filtersState, pageNumber: params};
            setFiltersState(fs);
            onFilterOffices(fs);
        },
        delete: (data: any) => {
            setIsConfirmOpen(true);
            setConfirmationData({mode: "single", data});
        },

        deleteGroup: () => {
            setIsConfirmOpen(true);
            setConfirmationData({mode: "multi", data: selectionModel});
        },


        addFormSubmit: (formData: any) => {
            onAddOffice(formData, filtersState);
            setIsAddDialogOpen(false);
        },

        editFormSubmit: (formData: any, updatedCallback: any) => {
            onEditOffice(formData, updatedCallback, filtersState);
            setIsEditDialogOpen(false);
        },
        deleteConfirm: () => {
            switch (confirmationData?.mode) {
                case "single":
                    onDeleteOffice(confirmationData.data, filtersState);
                    break;
                case "multi":
                    onDeleteGroupOffices(confirmationData.data);
                    break;
                default:
                    break;
            }
        },
        handlePageSizeChange: (itemsOnPage: number) => {
            let fs = {...filtersState, pageSize: itemsOnPage, pageNumber: 0};
            setFiltersState(fs);
            onFilterOffices(fs);
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

        {!hideToolbar &&  <Toolbar>
            <Button
                disabled={!hasAccessAddOffice}
                style={hasAccessAddOffice ? buttonStyle : buttonDisabledStyle}
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
            rows={offices}
            columns={columns}
            pageSize={filtersState.pageSize}
            paginationMode="server"
            rowsPerPageOptions={[20]}
            rowCount={total}
            components={{
                Pagination: CustomPagination,
            }}
            componentsProps = {{
                pagination: {
                    total: total,
                    onPageChange: handlers.handlePageChange,
                    count: Math.ceil(total/filtersState.pageSize),
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

        <AddOfficeDialog
            calendars={references['calendars']?.list || []}
            editedData={editedData}
            open={isIsAddDialogOpen}
            mode={editorFormMode}
            onCancel={() => {
                setIsAddDialogOpen(false)
            }}
            formSubmit={handlers.addFormSubmit}/>

        <EditOfficeDialog
            offices={offices}
            isOnlyView={!hasAccessEditOffice}
            calendars={references['calendars']?.list || []}
            editedData={editedData}
            open={IsEditDialogOpen}
            mode={editorFormMode}
            onCancel={() => {
                setIsEditDialogOpen(false)
            }}
            formSubmit={handlers.editFormSubmit}/>

        <Backdrop
            className={classes.backdrop}
            open={isOfficesLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </div>
}

const mapStateToProps = (state: AppRootState) => ({
    offices: getOffices(state),
    total: getTotal(state),
    isOfficesLoading: isOfficesLoading(state),
    hasAccessOffice: hasAccessOfficesSelector(state),
    hasAccessAddOffice: hasAccessAddOfficesSelector(state),
    hasAccessEditOffice: hasAccessEditOfficesSelector(state)
});

const mapDispatchToProps = (dispatch: any) => {
    return {
        onFilterOffices: (filter: OfficeFilterUIInterface) => {
            dispatch(reloadOffices(filter));
        },
        onAddOffice: (offices: OfficeInterface, filter: CalendarFilterUIInterface) => {
            dispatch(addOffice(offices, filter));
        },
        getOffices: (filter: OfficeFilterUIInterface) => {
            dispatch(reloadOffices(filter));
        },
        onDeleteOffice: (offices: OfficeInterface, filtersState: OfficeFilterUIInterface) => {

            dispatch(deleteOfficeAction(offices, filtersState))
        },
        onEditOffice: (offices: OfficeInterface, updatedCallback: any, filtersState: OfficeFilterUIInterface) => {
            dispatch(editOfficeAction(offices, updatedCallback, filtersState))
        },
        onDeleteGroupOffices: (offices: Array<OfficeInterface>) => {
            dispatch(deleteGroupOfficesAction(offices))
        },
    };
};

const connector = connect(mapStateToProps, mapDispatchToProps);

type PropsFromRedux = ConnectedProps<typeof connector>;

export default connector(PageOffices);
