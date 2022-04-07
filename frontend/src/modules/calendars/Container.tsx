import React, {useEffect, useMemo, useState} from 'react';
import {connect, ConnectedProps} from "react-redux";
import {AddCalendarDialog} from "./components/AddCalendarDialog";
import {EditCalendarDialog} from "./components/EditCalendarDialog";
import {
    addCalendar,
    deleteCalendarsAction,
    deleteGroupCalendarsAction,
    editCalendarsAction,
    reloadCalendars
} from "./actions";
import {CalendarFilterUIInterface, CalendarInterface} from "./types";
import {getCalendars, getTotal, isCalendarsLoading} from "./selectors";
import {AppRootState, TypedSelector} from "../../store";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import {EditorMode} from "../../store/admin/types";
import {Backdrop, Button, CircularProgress, IconButton, Menu, MenuItem, Toolbar} from "@material-ui/core";
import {DataGrid} from "@material-ui/data-grid";
import {DialogConfirmCalendar} from "../../common/dialogs/DialogConfirmCalendar";
import PopupState, {bindMenu, bindTrigger} from 'material-ui-popup-state';
import MoreVert from '@material-ui/icons/MoreVert';
import AddIcon from "@material-ui/icons/Add";
import {buttonDisabledStyle, buttonStyle} from '../../common/styles';
import {hasAccessAddCalendarsSelector, hasAccessEditCalendarsSelector} from "../employeesCheck/selectors";
import {SearchTextInput} from "../employeesCheck/components";
import SearchIcon from "@material-ui/icons/Search";
import CustomPagination from "../../common/customPagination/CustomPagination";

interface Props extends PropsFromRedux {
    hideToolbar?: boolean;
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
        toolbar: any,
        dataGrid: any,
        actionColumn: any,
        actionButton: any,
        dialogEdit: any,
        dialogConfirm: any,
        backdrop: any,
        searchButton: any,
    } =>
        createStyles({
            root: {
                height: "calc(100% - 202px)",
                width: '100%',
            },
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
                       calendars = [],
                       isCalendarsLoading,
                       total = 0,
                       onAddCalendar,
                       getCalendars,
                       onEditCalendar,
                       onDeleteCalendar,
                       onFilterCalendars,
                       hideToolbar,
                       onDeleteGroupCalendars,
                       hasAccessAddCalendar,
                       hasAccessEditCalendar,

                   }: Props) => {

    useEffect(() => {
        if (getCalendars) {
            getCalendars();
        }
    }, [getCalendars]);

    const defaultFilterState: CalendarFilterUIInterface = {
        pageNumber: 0,
        pageSize: 20,
        searchString: "",
        sort: [],
    };

    const [filtersState, setFiltersState] = React.useState<CalendarFilterUIInterface>(defaultFilterState);

    const classes = useStyles();
    const {t} = useTranslation();

    const [selectionModel, setSelectionModel] = useState<any[]>([]);

    const [isIsAddDialogOpen, setIsAddDialogOpen] = useState<boolean>(false);
    const [IsEditDialogOpen, setIsEditDialogOpen] = useState<boolean>(false);

    const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
    const [confirmationData, setConfirmationData] = useState<any>({mode: "", data: {}});


    const [editorFormMode] = useState<string>(EditorMode.CREATE);
    const [editedData, setEditedData] = useState<any>();

    useEffect(() => {
        handlers.searchSubmit();
    }, [filtersState.sort]);

    const offices = TypedSelector(state => state.references).refs['offices']?.list || [];

    const columns = useMemo(() => {
        return [
            {
                field: 'id',
                headerName: 'ID',
                width: 10,
                flex: 1,
                hide: true,
            },
            {
                field: 'name',
                resizable: false,
                flex: 1,
                headerName: t('Calendar'),
                renderCell: ({row: {name}}: any): string => {
                    return `${name} `;
                },
            },
            {
                field: 'offices',
                headerName: t('Office'),
                flex: 3,
                sortable: false,
                renderCell: ({row}: any) => {
                    if (row?.offices) {
                        let officesArray = row?.offices.map((a: string) => offices.find(({id}: any) => id === a)?.name);
                        return <div style={{
                            paddingTop: "3px",
                            maxHeight: "52px"
                        }}>{officesArray.reduce((a: string, b: string) => (!a ? "" : a + ", ") + (b ?? ""))}</div>
                    }
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
                                    <IconButton disabled={!hasAccessEditCalendar} {...bindTrigger(popupState)}>
                                        <MoreVert fontSize="inherit"/>
                                    </IconButton>
                                    <Menu {...bindMenu(popupState)}>
                                        <MenuItem onClick={() => {
                                            handlers.edit(params.row);
                                            popupState.close();
                                        }}>{t('Edit')}</MenuItem>
                                        {hasAccessAddCalendar ? <MenuItem onClick={() => {
                                            handlers.delete(params.row);
                                            popupState.close();
                                        }}>{t('Delete')}</MenuItem> : null}
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
        searchSubmit: () => {
            let fs = {...filtersState, pageNumber: 0};
            setFiltersState(fs);
            onFilterCalendars(fs);
        },
        filtersReset: () => {
            const filter = {...defaultFilterState};
            setFiltersState(filter);
            onFilterCalendars(filter);
        },
        add: () => {
            setIsAddDialogOpen(true);
        },
        edit: (data: any) => {
            setIsEditDialogOpen(true);
            if (!data.startWeekDay) {
                data.startWeekDay = 1;
            }
            setEditedData(data);
        },
        handlePageChange: (params: any) => {
            let fs = {...filtersState, pageNumber: params};
            setFiltersState(fs);
            getCalendars(fs);
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
            onAddCalendar(formData, filtersState);
            setIsAddDialogOpen(false);
        },

        editFormSubmit: (formData: any, updatedCallback: any) => {
            onEditCalendar(formData, updatedCallback, filtersState);
            setIsEditDialogOpen(false);
        },
        deleteConfirm: () => {
            switch (confirmationData?.mode) {
                case "single":
                    onDeleteCalendar(confirmationData.data, filtersState);
                    break;
                case "multi":
                    onDeleteGroupCalendars(confirmationData.data);
                    break;
                default:
                    break;
            }
        },
        handlePageSizeChange: (itemsOnPage: number) => {
            let fs = {...filtersState, pageSize: itemsOnPage, pageNumber: 0};
            setFiltersState(fs);
            getCalendars(fs);
        }
    }

    return <div className={classes.root}>
        {!hideToolbar && <Toolbar>
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
                disabled={!hasAccessAddCalendar}
                style={hasAccessAddCalendar ? buttonStyle : buttonDisabledStyle}
                startIcon={<AddIcon/>}
                color="secondary"
                variant="contained"
                onClick={handlers.add}
            >{t('Add')}</Button>
        </Toolbar>}
        <DataGrid
            className={classes.dataGrid}
            rows={calendars}
            sortingMode="server"
            onSortModelChange={handlers.handleSortModelChange}
            disableColumnMenu
            columns={columns}
            pageSize={filtersState.pageSize}
            paginationMode="server"
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

        <DialogConfirmCalendar
            open={isConfirmOpen}
            onYesClick={() => {
                setIsConfirmOpen(false);
                handlers.deleteConfirm();
            }}
            onNoClick={() => {
                setIsConfirmOpen(false)
            }}/>

        <AddCalendarDialog
            offices={offices}
            open={isIsAddDialogOpen}
            mode={editorFormMode}
            onCancel={() => {
                setIsAddDialogOpen(false)
            }}
            formSubmit={handlers.addFormSubmit}/>

        <EditCalendarDialog
            calendars={calendars}
            offices={offices}
            editedData={editedData}
            open={IsEditDialogOpen}
            mode={editorFormMode}
            onCancel={() => {
                setIsEditDialogOpen(false)
            }}
            formSubmit={handlers.editFormSubmit}/>

        <Backdrop
            className={classes.backdrop}
            open={isCalendarsLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </div>
}

const mapStateToProps = (state: AppRootState) => ({
    calendars: getCalendars(state),
    total: getTotal(state),
    isCalendarsLoading: isCalendarsLoading(state),
    hasAccessAddCalendar: hasAccessAddCalendarsSelector(state),
    hasAccessEditCalendar: hasAccessEditCalendarsSelector(state),
})

const mapDispatchToProps = (dispatch: any) => {
    return {
        onFilterCalendars: (filter: CalendarFilterUIInterface) => {
            dispatch(reloadCalendars(filter));
        },
        onAddCalendar: (calendar: CalendarInterface, filter: CalendarFilterUIInterface) => {
            dispatch(addCalendar(calendar, filter));
        },
        getCalendars: (filter: CalendarFilterUIInterface | null = null) => {
            dispatch(reloadCalendars(filter));
        },
        onDeleteCalendar: (calendars: CalendarInterface, filter: CalendarFilterUIInterface) => {
            dispatch(deleteCalendarsAction(calendars, filter))
        },
        onEditCalendar: (calendars: CalendarInterface, updatedCallback: any, filter: CalendarFilterUIInterface) => {
            dispatch(editCalendarsAction(calendars, updatedCallback, filter))
        },
        onDeleteGroupCalendars: (calendars: Array<CalendarInterface>) => {
            dispatch(deleteGroupCalendarsAction(calendars))
        },
    };
};

const connector = connect(mapStateToProps, mapDispatchToProps)

type PropsFromRedux = ConnectedProps<typeof connector>

export default connector(Container)
