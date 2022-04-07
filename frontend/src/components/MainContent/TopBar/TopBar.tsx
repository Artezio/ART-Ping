import React, {useState} from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Toolbar from '@material-ui/core/Toolbar';
import {useHistory, useLocation} from "react-router-dom";
import Link from '@material-ui/core/Link';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import HelpCenterIcon from '@material-ui/icons/HelpOutlined';
import IconButton from '@material-ui/core/IconButton';
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import {TypedSelector} from '../../../store';
import {useDispatch, useSelector} from 'react-redux';
import * as UserActions from '../../../store/user/actions';
import {useTranslation} from "react-i18next";
import AppLogo from "../../../assets/icons/AppLogo.svg";
import {topButtonStyle, topButtonHightlightStyle} from '../../../common/styles';
import {
    hasAccessEmployeesCheckSelector,
    hasAccessEmployeesMyChecksSelector
} from "../../../modules/employeesCheck/selectors";
import Notification from "../../../common/notification/notification";

const useStyles = makeStyles((theme: Theme): {
        root: any,
        menuButton: any,
        title: any,
        logoWrapper: any
    } =>
        createStyles({
            root: {
                flexGrow: 1,
            },
            menuButton: {
                marginRight: theme.spacing(2),
                verticalAlign: "middle"
            },
            title: {
                flexGrow: 1,
                display: "inline-block",
                marginLeft: "35px !important"
            },
            logoWrapper: {
                width: "30px",
                height: "30px",
                position: "absolute",
                display: "inline-block",
                backgroundImage: "url(" + AppLogo + ")",
                backgroundPositionX: 0,
                backgroundPositionY: 0,
                backgroundSize: "cover",
                backgroundRepeat: "no-repeat"
            }
        }),
);

const TopBar = () => {
    const currentUser = TypedSelector(state => state.user);
    const currentSettings = TypedSelector(state => state.system);
    const location = useLocation();
    const dispatch = useDispatch();
    const history = useHistory();
    const classes = useStyles();
    const {t} = useTranslation();
    const [isHelpDialogOpen, setIsHelpDialogOpen] = useState<boolean>(false);
    const hasAccessToEmployeesChecks = useSelector(hasAccessEmployeesCheckSelector);
    const hasAccessToEmployeesMyChecks = useSelector(hasAccessEmployeesMyChecksSelector);


    const seeds: any = [
        {
            path: "/",
            title: "My checks",
            permissions: [],
            hasAccess:hasAccessToEmployeesMyChecks,
        },
        {
            path: "/employee-checks",
            title: "Employee checks list",
            permissions: [],
            hasAccess:hasAccessToEmployeesChecks,
        },
        {
            path: "/admin",
            title: "Admin panel",
            permissions: [],
            hasAccess:hasAccessToEmployeesChecks,
        },
    ];
    const buttons = [];
    for (let key in seeds) {

        const el = seeds[key];
        if (el.hasAccess)
            buttons.push(<Button
                style={location.pathname == el.path ? topButtonHightlightStyle : topButtonStyle}
                key={key}

                onClick={() => {
                    history.push(el.path);
                }}
            >
                {t(el.title)}
            </Button>);
    }

    const username_full = currentUser.firstName + " " + currentUser.lastName;

    const handleLogout = () => {

        dispatch(UserActions.TryLogoutUser());
    }

    const handleHelp = () => {
        setIsHelpDialogOpen(!isHelpDialogOpen);
    }

    return (<>
        <Paper>
            <div className={classes.root}>
                <Toolbar>
                    <Link
                        className={classes.menuButton}
                        color="inherit"
                        aria-label="menu"
                        component="button"
                        variant="body2"
                        onClick={() => {
                            history.push("/");
                        }}
                    >
                        <div className={classes.logoWrapper}></div>
                        <Typography variant="h5" className={classes.title}>ART-Ping</Typography>
                    </Link>
                    <div style={{
                        flexGrow: 1
                    }}>
                        {buttons}
                    </div>
                    <Typography variant="body1">{username_full}</Typography>
                    <IconButton
                        onClick={handleLogout}
                        color="inherit"
                        title={t("Logout")}>
                        <ExitToAppIcon/>
                    </IconButton>
                    <Notification/>
                    <IconButton
                        onClick={handleHelp}
                        color="inherit"
                        title={t("Help")}>
                        <HelpCenterIcon/>
                    </IconButton>
                </Toolbar>
            </div>
        </Paper>
        <Dialog
            open={isHelpDialogOpen}
            maxWidth={false}
            onClose={handleHelp}
        >
            <DialogTitle>{t('Help')}</DialogTitle>
            <div style={{padding: "15px"}}>
                <pre>
                    <div>branch: {currentSettings?.appInfo?.branch}</div>
                    <div>commitHashFull: {currentSettings?.appInfo?.commitHashFull}</div>
                    <div>commitHashShort: {currentSettings?.appInfo?.commitHashShort}</div>
                    <div>commitDateTime: {currentSettings?.appInfo?.commitDateTime}</div>
                    <div>buildDateTime: {currentSettings?.appInfo?.buildDateTime}</div>
                </pre>
            </div>
        </Dialog>
    </>);
}

export default TopBar;
