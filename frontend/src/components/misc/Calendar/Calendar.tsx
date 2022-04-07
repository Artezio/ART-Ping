import React, {useMemo} from 'react';
import {useFormikContext} from "formik";
import {MONTHS, WEEK_DAYS_SHORT} from './src/shared';
import {format, isEqual, Locale, parse, startOfMonth} from 'date-fns';
import {DefaultMonthlyEventItem, MonthlyBody, MonthlyCalendar, MonthlyDay, MonthlyDayTypePopup,} from './src';
import {DatePicker} from "@material-ui/pickers";
import IconButton from '@material-ui/core/IconButton';
import ArrowForwardIosIcon from "@material-ui/icons/ArrowForwardIos";
import ArrowBackIosIcon from "@material-ui/icons/ArrowBackIos";
import {update} from "ramda";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme): {
    arrows:any;
}=>  createStyles({
    arrows:{
        display:"center"
}

}));




const LOCALE_PRESET: Locale = {
    code: 'ru',
    localize: {
        ordinalNumber: (args: any) => args,
        era: (args: any) => args,
        quarter: (args: any) => args,
        month: (args: any) => MONTHS[args],
        day: (args: any) => WEEK_DAYS_SHORT[args],
        dayPeriod: (args: any) => args
    },
    formatLong: {
        date: (args: any) => args,
        time: (args: any) => args,
        dateTime: (args: any) => args,
    },
    options: {
        weekStartsOn: 1,
    }
}

export type ICalendarEventProps = {
    title: string;
    type: string;
    date: Date | string;
}

export type CalendarMonthEventsList = ICalendarEventProps[];

export type ICalendarProps = {
    disabled?: boolean;
    title?: string;
    value?: CalendarMonthEventsList[] | null;
    weekendDays?: number[];
    name?: string;
    onChange?: (props: any) => void
    isCustom?: boolean;
    from?: Date;
    type?: string;
    onChanger?: any;
    readOnly: boolean;
}
const dateTimeZoneCorrection = (event: any) => {
    const format = new Date();
    let day = (event?.date) ? event?.date : event;
    return new Date(parse(day, 'yyyy-MM-dd', format).setMinutes(-format.getTimezoneOffset()));
}
const prepareEvents = (events: any) => {
    if (typeof events == "undefined") return events;
    return events.map((event: any) => {
        return {...event, date: dateTimeZoneCorrection(event)}
    });
}

const Calendar = (props: ICalendarProps) => {

    const [year, setYear] = React.useState(new Date());

    const handleDateChange = (date: any) => {
        setYear(date[0]);
    }
    const handleYearStep = (step: number) => {
        setYear(new Date(year.getFullYear() + step, 1))
    }


    const {values, setFieldValue} = useFormikContext<any>();
    const [MonthlyDayTypePopupState, setMonthlyDayTypePopup] = React.useState({
        pointerX: 0,
        pointerY: 0,
        day: null,
        weekendDays: values?.weekendDays ? values?.weekendDays : null,
        calendarEvent: null,
        isOpen: false,
        isCustom: Boolean(props.isCustom),
    });

    const [events, setEvents] = React.useState<CalendarMonthEventsList[]>([]);
    const isFieldUpdate = React.useRef(false);
    const currentMonthNumberToDate = (currentMonthNumber: number) => {
        return startOfMonth(new Date(year.getFullYear(), currentMonthNumber, 1, 0, 0, 0, 0));
    };
    React.useEffect(() => {
        if (!isFieldUpdate.current) {
            setEvents(prepareEvents(props.value) || []);
        }
        isFieldUpdate.current = false;
    }, [props.value]);

    const onDayClick = (mouseClickEvent: any, day: any) => {
        if (props.disabled) {
            setMonthlyDayTypePopup({...MonthlyDayTypePopupState, isOpen: false});
            return;
        }
        let calendarEvent: any = null;
        events.forEach((event: any) => {
            const eventDate = (typeof event?.date === 'string') ? dateTimeZoneCorrection(event) : event?.date;

            if (isEqual(eventDate, dateTimeZoneCorrection(format(day, 'yyy-MM-dd')))) {
                calendarEvent = event;
            }
        });

        const popupContent = {
            ...MonthlyDayTypePopupState,
            pointerX: mouseClickEvent.pageX,
            pointerY: mouseClickEvent.pageY,
            weekendDays: values?.weekendDays ? values?.weekendDays : null,
            day,
            calendarEvent,
            isOpen: true
        }
        setMonthlyDayTypePopup(popupContent);
    };

    const onDayTypeChange = (day: any, value: string, commentValue: string) => {
        const newEvents: any[] = [
            ...events
        ];
        const dayIndex = typeof newEvents?.findIndex === 'function' ? newEvents.findIndex((el: any) => {
            return isEqual(typeof el?.date === 'string' ?
                dateTimeZoneCorrection(el) : el?.date, dateTimeZoneCorrection(format(day, 'yyy-MM-dd')));
        }) : -1;
        if (dayIndex > -1) {
            newEvents[dayIndex] = {
                id: newEvents[dayIndex].id,
                title: commentValue || '',
                type: value,
                date: dateTimeZoneCorrection(format(day, 'yyy-MM-dd'))
            };
        } else {
            newEvents.push({
                title: commentValue || '',
                type: value,
                date: dateTimeZoneCorrection(format(day, 'yyy-MM-dd'))
            });
        }

        let returnable: ICalendarEventProps[] = [];
        newEvents.forEach((el: any) => {
            returnable = [...returnable, ...([el] || [])];
        });
        setEvents(newEvents);

        if (typeof props.onChange == "function") {
            try {
                props.onChange({
                    ...props,
                    target: {},
                    value: returnable
                });
            } catch (err) {
                console.error({err});
            }
        }

        if (props.name) {
            setFieldValue(props.name, returnable);
            isFieldUpdate.current = true;
        }
    };

    const prepareCalendar = () => {

        let rows = [];
        let currentMonthNumber = 0;
        for (let r = 1; r < 4; r++) {
            let months: any = [];
            for (let m = 1; m < 5; m++) {
                months.push(<div className="calendar-month-wrapper" key={m}>
                    <h2>{MONTHS[currentMonthNumber]}</h2>
                    <MonthlyCalendar
                        locale={LOCALE_PRESET}
                        currentMonth={currentMonthNumberToDate(currentMonthNumber)}
                        onCurrentMonthChange={() => {
                        }}
                    >
                        <MonthlyBody
                            events={events || []}
                            weekendDays={values && values?.weekendDays ? values?.weekendDays : []}
                        >
                            <MonthlyDay
                                onDayClick={onDayClick}
                                renderDay={data =>
                                    data.map((item: any, index: number) => (
                                        <DefaultMonthlyEventItem
                                            key={index}
                                            title={item.title}
                                            date={format(item.date, 'k:mm')}
                                        />
                                    ))
                                }
                            />
                        </MonthlyBody>
                    </MonthlyCalendar>
                </div>);
                currentMonthNumber++;
            }
            rows.push(<div className="calendar-row" key={r}>{months}</div>);
        }
        return rows;
    }

    return <div className="calendar-root-wrapper" style={{position: "relative", height: "655px"}}>

        <div className={"calendar-slider"} >
            <IconButton
                onClick={() => {
                    handleYearStep(-1);
                }}
            >
                <ArrowBackIosIcon/>
            </IconButton>
            {
                <DatePicker
                    label={"Выберите год"}
                    views={['year']}
                    value={year}
                    onChange={(value: any) => {
                        handleDateChange([value])
                    }}
                />
            }
            <IconButton
                onClick={() => {
                    handleYearStep(+1);
                }}
            >
                <ArrowForwardIosIcon/>
            </IconButton>
        </div>
        {props.title && <h1 className="calendar-header-wrapper">{props.title}</h1>}

        <MonthlyDayTypePopup {...MonthlyDayTypePopupState}
                             onChange={onDayTypeChange}
        />

        <div className="calendar-content-wrapper">
            {prepareCalendar()}
        </div>


    </div>
};

export default Calendar;
