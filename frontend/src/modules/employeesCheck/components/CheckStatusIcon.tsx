import * as React from 'react';
import {useTranslation} from "react-i18next";
import {
    DesktopAccessDisableIcon,
    DesktopWindowIcon,
    InProcessIcon,
    NoAnswerIcon,
    PlannedIcon,
    ValidCoords
} from "./IndicateCheck";
import {MuiThemeProvider, Tooltip} from "@material-ui/core";
import checkStatusConst from "./checkStatusConstants";
import {createStyles, createTheme, makeStyles, Theme,} from "@material-ui/core/styles";
import {useCallback} from "react";

const useStyles = makeStyles((theme: Theme): {
        coordsLink: any,
        checkIcon: any,
    } =>
        createStyles({
            coordsLink: {
                color: "#007AFF",
                cursor: "pointer",
                textDecoration: "underline"
            },
            checkIcon: {
                position: "relative",
                height: "15px",
                margin: "1px",
            },
        })
);
const theme = ({
    overrides: {
        MuiTooltip: {
            tooltip: {
                border: "solid #e5e7eb 1px",
                color: "#000",
                backgroundColor: "#fff"
            }
        }
    },
});

function CheckStatusIcon(props: any) {
    const classes = useStyles();

    const {status, startTime, responseTime, point} = props;

    const getStatusIcon = useCallback((statusType: string) => {

            if (statusType === "IN_PROGRESS") {
                return <InProcessIcon/>
            }
            if (statusType === "NO_RESPONSE") {
                return <NoAnswerIcon/>
            }
            if (statusType === "SUCCESS") {
                return <DesktopWindowIcon/>
            }
            if (statusType === "VALID") {
                return <ValidCoords/>
            }
            if (statusType === "NOT_SUCCESS") {
                return <DesktopAccessDisableIcon/>
            }
            if (statusType === "PLANNED") {
                return <PlannedIcon/>
            }
            return <DesktopWindowIcon/>;
        },
        [InProcessIcon, NoAnswerIcon, DesktopWindowIcon, ValidCoords, DesktopAccessDisableIcon, PlannedIcon])

    //
    const {t} = useTranslation();

    return (
        <MuiThemeProvider theme={theme}>
            <Tooltip
                interactive
                placement="top-end"
                title={
                    <React.Fragment>
                        <div>{t('Status')}: <b>{status ? t(checkStatusConst.get(status)) : '-'}</b></div>
                        <div>{t('Start check time')}: <b>{startTime ? startTime.substr(0, 8) : '-'}</b></div>
                        <div>{t('Response get time')}: <b>{responseTime ? responseTime.substr(0, 8) : '-'}</b>
                        </div>
                        <b> {point?.valid ?
                            <a target="_blank"
                               className={classes.coordsLink}
                               href={`https://yandex.ru/maps/?pt=${point.longitude},${point.latitude}&z=15&l=map`}>Местоположение</a> : null}</b>
                    </React.Fragment>}
            >
                <div
                    className={classes.checkIcon}> {getStatusIcon(status)} {point?.valid ? getStatusIcon('VALID') : null} </div>
            </Tooltip>
        </MuiThemeProvider>
    );
}

export default React.memo(CheckStatusIcon);
