import React from 'react';
import { makeStyles, createStyles, Theme } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';
import { useTranslation } from "react-i18next";
import Typography from '@material-ui/core/Typography';
const useStyles = makeStyles((theme: Theme) :{root: any, wrapper: any}=>
    createStyles({
        root: {
            position: "absolute",
            top: "0px",
            left: "0px",
            zIndex: 100,
            width: "100%",
            height: "100%"

        },
        wrapper: {
            margin: 0,
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: "30%",
            height: "200px",
            textAlign: "center"
        }
    }),
);
const AppLoadingScreen = () => {
    const classes = useStyles();
    const { t } = useTranslation();
    return (<div className={classes.root}>
        <div className={classes.wrapper}>
            <CircularProgress />
            <Typography variant="h5">{t('Application loading') + "..."}</Typography>
        </div>

    </div>);
}

export default AppLoadingScreen;