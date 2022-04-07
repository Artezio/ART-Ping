import React from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import ArrowBackIosIcon from '@material-ui/icons/ArrowBackIos';
import ArrowForwardIosIcon from '@material-ui/icons/ArrowForwardIos';
import IconButton from '@material-ui/core/IconButton';
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import {Input, TextFieldProps} from "@material-ui/core";
import {add, endOfDay, endOfMonth, endOfWeek, format, startOfDay, startOfMonth, startOfWeek} from 'date-fns';
import {useTranslation} from "react-i18next";
import {DatePicker} from "@material-ui/pickers";
import WeekDatePicker from "./WeekDatePicker";
import {weekStartsOn} from "../utils";


const useStyles = makeStyles((theme: Theme): { root: any, picker: any } =>
    createStyles({
        root: {
            marginLeft: "10px",
            width: '550px',
            display: "flex"
        },
        picker: {
            width: "300px"
        },
    })
);

export interface DateRangeProps {
    from?: Date;
    to?: Date;
    type?: string;
    onChange?: any;
}

const DateRange = (
    props: DateRangeProps) => {
    const {t} = useTranslation();


    const classes = useStyles();

    const currentDate = new Date();
    const defaultPeriod: any = [
        props.from ? props.from :currentDate,
        props.to ? props.to : endOfWeek(currentDate, {weekStartsOn})
    ];

    const val = 'dd/MM/yyyy'

    const renderInput = (props: TextFieldProps): any => {

        return <Input
            type="text"
            onClick={props.onClick}
            value={`${format(widgetState.period[0], val)} - ${format(widgetState.period[1], val)}`}
            onChange={props.onChange}

        />
    };

    const newInputFrom = (props: TextFieldProps): any => {
        return <Input
            type="text"
            onClick={props.onClick}
            value={`${format(widgetState.period[0], val)}`}
            onChange={props.onChange}
        />
    };
    const newInputTo = (props: TextFieldProps): any => {
        return <Input
            type="text"
            onClick={props.onClick}
            value={`${format(widgetState.period[1], val)}`}
            onChange={props.onChange}
        />
    };


    const [widgetState, setWidgetState] = React.useState({
        period: defaultPeriod,
        type: props.type || 'week'
    });


    const updateState = (newState: any) => {
        const consolidated = Object.assign({}, widgetState, newState);
        setWidgetState(consolidated);
        if (typeof props.onChange == 'function') {

            props.onChange({from: consolidated.period[0], to: consolidated.period[1], type: consolidated.type});
        }
    }

    const handlePeriodTypeChange = (type: string) => {
        let period = [...widgetState.period];
        switch (type) {
            case 'week':
                period[0] = (startOfWeek(new Date(), {weekStartsOn}))
                period[1] = endOfWeek(new Date(), {weekStartsOn})

                break;
            case 'month':
                period[0] = (startOfMonth(period[0]))
                period[1] = endOfMonth(period[0])
                break;
            case 'period':
            default:
                break;
        }

        updateState({period, type});
    }

    const handleDateChange = (date: any) => {
        let period = [...date];
        if (!date[0]) {
            return;
        }
        switch (widgetState.type) {
            case 'week':
                period = [
                    (startOfWeek(date[0], {weekStartsOn})),
                    endOfWeek(date[0], {weekStartsOn})
                ];
                break;
            case 'month':
                period = [
                    (startOfMonth(date[0])),
                    endOfMonth(date[0])
                ];
                break;
            case 'period':
                period = [
                    startOfDay(date[0]),
                    endOfDay(date[0])
                ];
                break;
        }

        updateState({period, type: widgetState.type});

    }

    const handleDateStartChange = (date: any) => {
        updateState({period: [startOfDay(date), widgetState.period[1]], type: "period"});
    }

    const handleDateEndChange = (date: any) => {
        updateState({period: [widgetState.period[0], endOfDay(date)], type: "period"})
    }

    const handlePeriodStep = (way: number) => {
        let period = [...widgetState.period];
        switch (widgetState.type) {
            case 'week':
                period[0] = (startOfWeek(add(period[0], {'weeks': way}), {weekStartsOn}))
                period[1] = endOfWeek(period[0], {weekStartsOn})
                break;
            case 'month':
                period[0] = (startOfMonth(add(period[0], {'months': way})))
                period[1] = endOfMonth(period[0])
                break;
            case 'period':
                period[0] = startOfDay(add(period[0], {"days": way}))
                period[1] = endOfDay(period[0])
                break;
        }
        updateState({period});
    }

    const preparePeriodButtons = () => {
        const buttons = [];
        const seeds = [
            {
                key: 'week',
                title: t('Week')
            },
            {
                key: 'month',
                title: t('Month')
            },
            {
                key: 'period',
                title: t('Period')
            }
        ];
        for (let el in seeds) {
            buttons.push(<Button
                key={el}
                variant={widgetState.type === seeds[el].key ? "contained" : "outlined"}
                data-key={seeds[el].key}
                onClick={() => {
                    handlePeriodTypeChange(seeds[el].key)
                }}
            >

                {seeds[el].title}
            </Button>);
        }
        return <ButtonGroup
            color="primary"
            aria-label="outlined primary button group"
        >
            {buttons}
        </ButtonGroup>
    }

    // let minDate = currentDate;
    return <div
        className={classes.root}
    >
        {preparePeriodButtons()}
        <IconButton
            disabled={widgetState.type == 'period'}
            title={t("Prev")}
            onClick={() => {
                handlePeriodStep(-1);
            }}
        >
            <ArrowBackIosIcon/>
        </IconButton>


        <div
            className={classes.picker}
        >
            {
                widgetState.type === 'week' &&
                <WeekDatePicker
                    //@ts-ignore
                    TextFieldComponent={renderInput}
                    // minDate={minDate}
                    value={widgetState.period}
                    onChange={(value: any) => {
                        handleDateChange([value])
                    }}
                    views={["date"]}
                    okLabel={<span style={{ color: "#f44336" }}>{t('Ok')}</span>}
                    cancelLabel={<span style={{ color: "#f44336" }}>{t('Cancel')}</span>}


                />
            }
            {
                widgetState.type === 'month' &&
                <DatePicker
                    // minDate={minDate}
                    TextFieldComponent={renderInput}
                    value={widgetState.period}
                    onChange={(value: any) => {
                        handleDateChange([value])
                    }}
                    views={["month"]}
                    okLabel={<span style={{ color: "#f44336" }}>{t('Ok')}</span>}
                    cancelLabel={<span style={{ color: "#f44336" }}>{t('Cancel')}</span>}

                />
            }
            {
                widgetState.type === 'period' &&
                <DatePicker
                    // minDate={minDate}
                    TextFieldComponent={newInputFrom}
                    value={widgetState.period.from}
                    onChange={(newValue) => {
                        handleDateStartChange(newValue);
                    }}
                    views={["date"]}
                    okLabel={<span style={{ color: "#f44336" }}>{t('Ok')}</span>}
                    cancelLabel={<span style={{ color: "#f44336" }}>{t('Cancel')}</span>}

                />
            }
            {
                widgetState.type === 'period' &&
                <DatePicker
                    TextFieldComponent={newInputTo}
                    value={widgetState.period.to}
                    onChange={(newValue) => {
                        handleDateEndChange(newValue);
                    }}
                    views={["date"]}
                    okLabel={<span style={{ color: "#f44336" }}>{t('Ok')}</span>}
                    cancelLabel={<span style={{ color: "#f44336" }}>{t('Cancel')}</span>}
                />
            }
        </div>

        <IconButton
            disabled={widgetState.type == 'period'}
            title={t("Next")}
            onClick={() => {
                handlePeriodStep(1);
            }}
        >
            <ArrowForwardIosIcon/>
        </IconButton>
    </div>;
};

export default DateRange