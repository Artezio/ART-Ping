import React, {useMemo} from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import {TypedSelector} from '../../store';
import AppLogo from "../../assets/icons/AppLogo.svg";
import MainAuthForm from "./MainAuthForm/MainAuthForm";
import RecoveryPassword from "./RecoveryPassword/RecoveryPassword";
import {Route, Switch, Redirect} from "react-router-dom";
import ConfirmNewPassword from "./ConfirmNewPassword/ConfirmNewPassword";
import UserPingPanel from "../MainContent/UserPingPanel/UserPingPanel";
import {ADMIN} from "../../store/user/constants";

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        root: {
            position: "absolute",
            top: "0px",
            left: "0px",
            zIndex: 100,
            width: "100%",
            height: "100%",
            "& .MuiButton-containedPrimary": {
                color: "#fff",
                backgroundColor: "#DB152C"
            },
            "& .MuiTypography-body1": {
                fontSize: "14px"
            },
        },
        paper: {
            margin: 0,
            height: "100%",
            maxWidth: "520px",
            paddingLeft: "116px",
            paddingRight: "116px",
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
        },
        appInfo: {
            position: "absolute",
            bottom: "50px",
            left: "50px",
            color: "rgba(133,133,133,0.7)",
        },
        title: {
            display: "inline-block",
            pointerEvents: "none",
            color: "#1A1F29",
            fontSize: "30px",
            fontWeight: "bold"
        },
        formWrapper: {
            position: "relative",
            top: "15vh",
        },
        logoWrapper: {
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            marginBottom: "20vh",
        },
        logo: {
            width: "30px",
            height: "30px",
            display: "inline-block",
            backgroundImage: "url(" + AppLogo + ")",
            backgroundPositionX: 0,
            backgroundPositionY: 0,
            backgroundSize: "cover",
            backgroundRepeat: "no-repeat"
        }
    })
);

const AuthForm = () => {
    const classes = useStyles();

    const currentSettings = TypedSelector(state => state.system);

    return (
        <div className={classes.root}>
            <Paper className={classes.paper}>
                <div className={classes.formWrapper}>
                    <div className={classes.logoWrapper}>
                        <div className={classes.logo}></div>
                        <Typography variant="h5" className={classes.title}>ART-Ping</Typography>
                    </div>
                    <Switch>
                        <Route exact path="/recovery/:key">
                            <ConfirmNewPassword/>
                        </Route>
                        <Route exact path="/recovery-password">
                            <RecoveryPassword/>
                        </Route>
                        <Route exact path="/">
                            <MainAuthForm/>
                        </Route>
                        <Redirect to="/"/>


                    </Switch>
                </div>
            </Paper>
            <div className={classes.appInfo}>
            <pre>
                <div>branch: {currentSettings?.appInfo?.branch}</div>
                <div>commitHashFull: {currentSettings?.appInfo?.commitHashFull}</div>
                <div>commitHashShort: {currentSettings?.appInfo?.commitHashShort}</div>
                <div>commitDateTime: {currentSettings?.appInfo?.commitDateTime}</div>
                <div>buildDateTime: {currentSettings?.appInfo?.buildDateTime}</div>
            </pre>
            </div>
        </div>
    );
}

export default AuthForm;

