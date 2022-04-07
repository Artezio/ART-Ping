import React from 'react';
import { Radio, RadioGroup } from '@material-ui/core';
import { WEEKEND_DAYS } from '../shared';
import { format, getDay } from 'date-fns';
import { useTranslation } from "react-i18next";
import { TypedSelector } from '../../../../../store';
import TextField from '@material-ui/core/TextField';
import { CALENDAR_DAY_TYPE } from '../../index'
import Button from "@material-ui/core/Button";
import {buttonDisabledStyle, buttonStyle} from "../../../../../common/styles";
import DialogActions from "@material-ui/core/DialogActions";
import Box from '@material-ui/core/Box';
import {hasAccessAddEditVacationsSelector} from "../../../../../modules/employeesCheck/selectors";
import {useSelector} from "react-redux";
import CloseIcon from "@mui/icons-material/Close";

type MonthlyDayTypePopupProps = {
    pointerX: number;
    pointerY: number;
    weekendDays?: any;
    calendarEvent?: any;
    day: any;
    isOpen: boolean;
    onChange?: (day: any, value: string, commentValue: string) => void,
    isCustom: boolean;
}

export const MonthlyDayTypePopup = ({ pointerX, pointerY, isOpen, day, onChange, calendarEvent = null, weekendDays = null, isCustom }: MonthlyDayTypePopupProps) => {
    const hasAccessAddEditVacations =useSelector(hasAccessAddEditVacationsSelector)
    const popupFrame = React.useRef<HTMLDivElement>(null);
    const { t } = useTranslation();
    const WEEKENDS = weekendDays || WEEKEND_DAYS;
    const currentSettings = TypedSelector(state => state.system).list;
    const dayTypesList = currentSettings.find((item: any) => item.name === 'dayTypesList');
    const prepareValue = () => {
        const dayNumber = getDay(day);
        let value = WEEKENDS.includes(dayNumber) ? CALENDAR_DAY_TYPE.WEEKEND.toString() : CALENDAR_DAY_TYPE.WORKING_DAY.toString();
        if (calendarEvent) {
            value = calendarEvent.type
        }
        return value;
    }
    const [commentValue, setCommentValue] = React.useState(calendarEvent?.title || '');
    const [value, setValue] = React.useState(prepareValue());
    const [_isOpen, set_IsOpen] = React.useState(isOpen);
    const _onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const value = (event.target as HTMLInputElement).value
        setValue(value);
    };
    const saveDayType = () => {
        typeof onChange == "function" ? onChange(day, value, commentValue) : (() => { })();
        setTimeout(() => {
            setCommentValue('');
            set_IsOpen(false)
        }, 500);
    }

    React.useEffect(() => {
        setValue(prepareValue());
        set_IsOpen(isOpen || false);
    }, [day, isOpen]);
    React.useMemo(() => {
        setCommentValue(calendarEvent?.title || '');
    }, [calendarEvent]);
    React.useCallback(() => {
        calculatePopupPosition(pointerX, pointerY)
    }, [pointerX, pointerY])
    const options = [];
    for (let i in dayTypesList?.value) {
        if (isCustom && dayTypesList?.value[i].personalCalendarSetting) {
            options.push(<li key={i}><Radio value={dayTypesList?.value[i].name} onChange={_onChange} />{dayTypesList?.value[i].value}</li>)
        } else if (!isCustom && dayTypesList?.value[i].officeCalendarSetting){
            options.push(<li key={i}><Radio value={dayTypesList?.value[i].name} onChange={_onChange} />{dayTypesList?.value[i].value}</li>)
        }
    }
    const calculatePopupPosition = (pointerX: number, pointerY: number) => {
        let popupPositionX = 0;
        let popupPositionY = 0;
        if (popupFrame?.current?.offsetParent) {
            const parentFrameRect = popupFrame?.current?.offsetParent?.getBoundingClientRect();
            const popupFrameRect = popupFrame?.current?.getBoundingClientRect();
            popupPositionX = pointerX - parentFrameRect.left;
            popupPositionY = pointerY - parentFrameRect.top;
            if ((pointerX + popupFrameRect.width) > parentFrameRect.right) {
                popupPositionX -= popupFrameRect.width;
            }
            if ((pointerY + popupFrameRect.height) > parentFrameRect.bottom) {
                popupPositionY -= popupFrameRect.width;
            }
        }
        return {
            popupPositionX,
            popupPositionY
        }
    }
    const {popupPositionY, popupPositionX} = calculatePopupPosition(pointerX, pointerY);
    return <div
        className="calendar-monthly-popup"
        ref={popupFrame}
        style={{
            position: "absolute",
            top: popupPositionY,
            left: popupPositionX,
            border: "solid #e5e7eb 2px",
            backgroundColor: "#ffffff",
            visibility: (_isOpen ? "visible" : "hidden"),
            zIndex: 10000,
            minWidth: 200,
            padding: "5px"
        }}
    >
        <div
            className="calendar-monthly-popup-header"
            style={{
                display: "flex"
            }}
        >
            <h2
                style={{
                    flex: "1"
                }}
            >{t('Day type')} {day ? format(day, 'yyy-MM-dd') : "****"}</h2>
            <div
                style={{
                    cursor: "pointer"
                }}
                onClick={() => {
                setCommentValue('');
                set_IsOpen(false); }}><CloseIcon fontSize="small"/></div>
        </div>
        <div
            className="calendar-monthly-popup-content"
        >
            <RadioGroup name="dayType" value={value}>
                <ul>{options}</ul>
            </RadioGroup>
        </div>
        {isCustom ? null :
            <Box sx={{display: 'flex', mx: "auto", width: '95%'}}>
            <TextField
            label={"Комментарий"}
            placeholder={"Комментарий"}
            value={commentValue}
            hiddenLabel
            multiline
            onChange={(e: any) => {
                setCommentValue(e.target.value);
            }}
            />
            </Box>
        }

        <DialogActions>
            <Button
                disabled={!hasAccessAddEditVacations}
                style={hasAccessAddEditVacations ? buttonStyle : buttonDisabledStyle}
                onClick={() => {
                    set_IsOpen(false);
                    setCommentValue('');
                }}
                color="primary"
            >
                {t('Cancel')}
            </Button>
            <Button style={hasAccessAddEditVacations ? buttonStyle : buttonDisabledStyle}
                    disabled={!hasAccessAddEditVacations}
                    onClick={saveDayType} color="secondary" variant="contained">
                {t('Save')}
            </Button>
        </DialogActions>
    </div>
}
