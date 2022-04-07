import {DataGrid} from '@material-ui/data-grid';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import {add, differenceInDays, format, parse} from 'date-fns';
import { ru } from 'date-fns/locale'
import {useTranslation} from "react-i18next";
import {TypedSelector} from "../../../store";
import React, {useMemo, useState, useRef, useCallback} from "react";
import CustomPagination from "../../../common/customPagination/CustomPagination";
import CheckStatusIcon from "./CheckStatusIcon";
import {CheckedIconFalse, CheckedIconTrue} from "./IndicateCheck";
import FunctionsIcon from '@mui/icons-material/Functions';
import StartIcon from '@mui/icons-material/Start';
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded';
import CancelRoundedIcon from '@mui/icons-material/CancelRounded';
import DoDisturbOnRoundedIcon from '@mui/icons-material/DoDisturbOnRounded';

const useStyles = makeStyles((theme: Theme): {
        root: any,
        dataGrid: any,
        eventTypeText: any,
        checksCell: any,
        statistic: any,
        statisticOpen: any
    } =>
        createStyles({
            root: {
                "& .MuiDataGrid-cell": {
                    borderRight: "1px solid rgba(224, 224, 224, 1)",
                    lineHeight: "0px !important",
                    display: "flex",
                    flexWrap: "wrap",
                },
                "& .MuiDataGrid-cell:last-child": {
                    borderRight: "none"
                },
                "& .MuiDataGrid-columnHeaderTitle": {
                    fontWeight: "bold"
                },
                "& .MuiDataGrid-window": {
                    overflowX: "scroll",
                },
                width: '100%',
            },
            dataGrid: {
                position: 'absolute',
                width: "100%",
                height: "calc(100% - 192px)",
                backgroundColor: "#FFFFFF",
            },
            eventTypeText: {
                color: "#0000FF",
                margin: "0 auto",
                display: "flex",
                alignItems: "center",
                '& p': {
                    margin: '0 5px'
                }
            },
            checksCell: {
                display: "flex",
                flexWrap: "wrap",
            },
            statistic: {
                position: 'absolute',
                right: "0px",
                width: '78px',
                height: "calc(100% - 192px)",
                backgroundColor: "#FFFFFF",
                borderLeftWidth: "0px",
                "& .MuiDataGrid-columnHeaderTitle": {
                    paddingLeft: "7px"
                },
                "& .MuiDataGrid-cell": {
                    fontSize: "12px"
                },
                "& .MuiDataGrid-windowContainer": {
                    borderLeft: "1px solid rgba(224, 224, 224, 1)"
                },
            },
            statisticOpen: {
                width: '360px',
            },
        })
);


const EmployeesTable = ({
                            startPeriod = new Date(),
                            endPeriod = new Date(),
                            rows = [],
                            ...props
                        }: any) => {
    const classes = useStyles();
    const {t} = useTranslation();

    const defaultColumnParams = {
        disableColumnMenu: true,
        width: 100,
    }
    const refMainTable = useRef<any>();
    const refStatisticTable = useRef<any>();
    const currentSettings = TypedSelector(state => state.system).list;
    const dayTypesList = currentSettings.find((item: any) => item.name === 'dayTypesList')?.value;
    const [isStatisticOpen, setIsStatisticOpen] = useState(false);
    const [isMainTableFocused, setIsMainTableFocused] = useState(false);
    const [isScrollable, setIsScrollable] = useState(false);
    const handleShowStatistic = () => {
        setIsStatisticOpen(!isStatisticOpen);
    }
    const handleTableFocus = (isFocused: boolean) => {
        if (isFocused !== isMainTableFocused) {
            setIsMainTableFocused(isFocused)
        }
    }
    const handleRowsScroll = () => {
        if (refMainTable.current && refStatisticTable.current) {
            const scrollMainTableArea = refMainTable.current.querySelector(".MuiDataGrid-window");
            const scrollStatisticTableArea = refStatisticTable.current.querySelector(".MuiDataGrid-window");
            if (scrollMainTableArea && scrollStatisticTableArea) {
                if (isMainTableFocused) {
                    scrollStatisticTableArea.scrollTop = scrollMainTableArea.scrollTop;
                } else {
                    scrollMainTableArea.scrollTop = scrollStatisticTableArea.scrollTop;
                }
            }
        }
    }
    const columns = useMemo(() => {
        const columnsCheck = [
            {
                field: 'Active',
                headerName: t("Active"),
                width: 130,
                align: 'center',
                renderCell:({row:{active}}:any) =>{
                    if(active===true){
                        return <CheckedIconTrue/>;
                    }
                    return <CheckedIconFalse/>;
                }
            },
            {
                field: 'id',
                key: '-999',
                headerName: 'ID',
                width: 170,
                hide: true
            },
            Object.assign({}, defaultColumnParams, {
                field: 'lastName', key: '-998',
                headerName: t('Employee'),
                width: 200,
                renderCell: ({row: {firstName, lastName}}: any) => {
                    return `${lastName} ${firstName}`

                },
            }),
        ]
        const now = new Date();
        let dateFrom = typeof startPeriod == 'string' ? parse(startPeriod, "yyyy-MM-dd", now) : startPeriod;
        let dateTo = typeof endPeriod == 'string' ? parse(endPeriod, "yyyy-MM-dd", now) : endPeriod;
        const diff = differenceInDays(dateTo, dateFrom);

        for (let n = 0; n <= diff; n++) {
            const cDayStr = format(dateFrom, "yyyy-MM-dd")
            const colKey = "days." + cDayStr;
            // @ts-ignore
            columnsCheck.push(Object.assign({}, defaultColumnParams, {
                field: colKey,
                sortable: false,
                key: colKey,
                headerName: format(dateFrom, "EE dd", {locale: ru}),
                width: 140,
                renderCell: ({row}: any) => {
                    const filteredDates: Array<any> = row.dates.filter((day: any) => {
                        return day.date == cDayStr
                    });
                    if (filteredDates.length === 0) {
                        return ''
                    }
                    if (filteredDates[0]?.type && filteredDates[0]?.type === "WORKING_DAY") {
                        return <div className={classes.checksCell}>
                            {filteredDates[0]?.tests.map(({status, startTime, responseTime, point}: any) => {
                                return <CheckStatusIcon
                                    status={status}
                                    startTime={startTime}
                                    responseTime={responseTime}
                                    point={point}
                                />
                            })}
                        </div>
                    }
                    const defaultDayTypeId = dayTypesList.findIndex((item: any) => {
                        return item.name === filteredDates[0].type;
                    })
                    if (filteredDates[0]?.type && filteredDates[0]?.type === "WEEKEND"){

                        return <div className={classes.eventTypeText}>
                            <p>{dayTypesList[defaultDayTypeId].value.charAt(0)}</p>
                            {filteredDates[0]?.tests.map(({status, startTime, responseTime, point}: any) => {
                            return <CheckStatusIcon
                                status={status}
                                startTime={startTime}
                                responseTime={responseTime}
                                point={point}
                            />
                        })}
                        </div>
                    }

                },
            }))
            dateFrom = add(dateFrom, {'days': 1});
        }
        return columnsCheck

    }, [startPeriod, endPeriod, dayTypesList]);

    const rowsStatistic = useMemo(() => {
        const calculatedStatistic: any[] = [];
        rows.forEach((user: any, index: number) => {
            let statisticObj = {
                id: index,
                'NO_RESPONSE': 0,
                'SUCCESS': 0,
                'NOT_SUCCESS': 0,
                'TOTAL': 0
            };
            user.dates.forEach((date: any) => {
                if (date?.tests?.length > 0) {
                    date.tests.forEach((ev: any) => {
                        if (ev.status === 'NO_RESPONSE') {
                            statisticObj['NO_RESPONSE'] +=1;
                            statisticObj['TOTAL'] +=1;
                        }
                        if (ev.status === 'NOT_SUCCESS') {
                            statisticObj['NOT_SUCCESS'] +=1;
                            statisticObj['TOTAL'] +=1;
                        }
                        if (ev.status === 'SUCCESS') {
                            statisticObj['SUCCESS'] +=1;
                            statisticObj['TOTAL'] +=1;
                        }
                    })
                }
            })
            calculatedStatistic.push(statisticObj);
        })
        return calculatedStatistic
    }, [rows]);

    const columnsStatistic = useMemo(() => {
        const columns = [
            {
                field: 'controlStatistic',
                key: '-994',
                headerName: <StartIcon style={{transform: `rotate(${isStatisticOpen ? 0 : 180}deg)`}}
                                       onClick={handleShowStatistic}
                />,
                width: 60,
                hide: false,
                sortable: false,
            },
            {
                field: 'successStatistic',
                key: '-998',
                headerName: <CheckCircleRoundedIcon style={{color: '4CAF50'}}/>,
                width: 70,
                align: "center",
                hide: !isStatisticOpen,
                sortable: false,
                renderCell: ({row: {SUCCESS, TOTAL}}: any) => {
                    return TOTAL > 0 ? `${SUCCESS}(${((SUCCESS/TOTAL)*100).toFixed(1)}%)` : `-`
                },
            },
            {
                field: 'notSuccessStatistic',
                key: '-997',
                headerName: <DoDisturbOnRoundedIcon/>,
                width: 70,
                align: "center",
                hide: !isStatisticOpen,
                sortable: false,
                renderCell: ({row: {NOT_SUCCESS, TOTAL}}: any) => {
                    return TOTAL > 0 ? `${NOT_SUCCESS}(${((NOT_SUCCESS/TOTAL)*100).toFixed(1)}%)` : `-`
                },
            },
            {
                field: 'noResponseStatistic',
                key: '-996',
                headerName: <CancelRoundedIcon style={{color: '#F00'}}/>,
                width: 70,
                align: "center",
                hide: !isStatisticOpen,
                sortable: false,
                renderCell: ({row: {NO_RESPONSE, TOTAL}}: any) => {
                    return TOTAL > 0 ? `${NO_RESPONSE}(${((NO_RESPONSE/TOTAL)*100).toFixed(1)}%)` : `-`
                },
            },
            {
                field: 'sumStatistic',
                key: '-995',
                headerName: <FunctionsIcon/>,
                width: 70,
                align: "center",
                hide: !isStatisticOpen,
                sortable: false,
                renderCell: ({row: {TOTAL}}: any) => {
                    return `${TOTAL}`
                },
            },
        ];
        return columns

    }, [startPeriod, endPeriod, dayTypesList, isStatisticOpen, rows]);

    return <div className={classes.root}>
        {columns.length > 0 &&
        <div>
            <DataGrid
                sortingMode="server"
                onSortModelChange
                columnBuffer={40}
                rowBuffer={100}
                onRowEnter={() => handleTableFocus(true)}
                disableColumnMenu
                className={classes.dataGrid}
                showCellRightBorder
                rows={rows}
                scrollbarSize={20}
                ref={refMainTable}
                 onStateChange={(state)=> {
                     handleRowsScroll();
                    if (state.state.scrollBar.hasScrollX !== isScrollable) {
                         setIsScrollable(state.state.scrollBar.hasScrollX);
                     }
                 }}
                columns={columns}
                pageSize={props.filters.pageSize}
                checkboxSelection={true}
                {...props}
                paginationMode="server"
                components={{
                    Pagination: CustomPagination,
                }}
                componentsProps={{
                    pagination: {
                        total: props.total,
                        onPageChange: props.handlePageChange,
                        count: Math.ceil(props.total / props.filters.pageSize),
                        page: props.filters.pageNumber,
                        itemsOnPage: props.filters.pageSize,
                        onItemsOnPageChange: props.onItemsOnPageChange
                    }
                }}
                rowsPerPageOptions={[props.filters.pageSize]}
                rowCount={props.total}
            />
            <DataGrid
                disableColumnMenu
                columnBuffer={40}
                rowBuffer={100}
                ref={refStatisticTable}
                onRowEnter={() => handleTableFocus(false)}
                pageSize={props.filters.pageSize}
                onStateChange={()=>handleRowsScroll()}
                className={`${classes.statistic} ${isStatisticOpen ? classes.statisticOpen : ''}`}
                rows={rowsStatistic}
                scrollbarSize={isScrollable ? 20 : 17}
                hideFooterRowCount={true}
                showCellRightBorder
                hideFooterSelectedRowCount={true}
                hideFooterPagination={true}
                columns={columnsStatistic}
                {...props}
                page={0}
            />
        </div>
        }
        {columns.length < 1 && <div
            className="no-data">
            No data
        </div>}
    </div>
}

export default React.memo(EmployeesTable)

