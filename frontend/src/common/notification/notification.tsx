import * as React from 'react';
import {useTranslation} from "react-i18next";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import ChatIcon from '@mui/icons-material/Chat';
import {useEffect, useMemo, useState} from "react";
import {requestNotifications, markReadNotification} from "./actions";

interface INotification {
    id: string;
    text: string;
    type: string;
    viewed: boolean;
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
        snackbar: any,
        snackbarCase: any,
        snackbarCaseClose: any,
        actionButtons: any,
        notificationText: any,
        notificationCount: any,
        emptyList: any,
    } =>
        createStyles({
            root: {
                position: "relative",
                zIndex: 100
            },
            snackbarCase: {
                position: "absolute",
                width: "400px",
                maxHeight: "80vh",
                top: "40px",
                right: "30px",
                padding: "0px 22px 0px 10px",
                border: "1px solid #e5e7eb",
                borderRadius: "5px",
                backgroundColor: "#e5e5e5",
                overflow: "auto",
                boxShadow: "0px 2px 1px -1px rgba(0,0,0,0.2)," +
                    "0px 1px 1px 0px rgba(0,0,0,0.14)," +
                    "0px 1px 3px 0px rgba(0,0,0,0.12)"
            },
            snackbarCaseClose: {
                position: "absolute",
                cursor: "pointer",
                top: "0px",
                right: "2px",
            },
            snackbar: {
                width: "100%",
                position: "relative",
                border: "1px solid #e5e7eb",
                borderRadius: "5px",
                padding: "5px",
                margin: "10px 0px",
                backgroundColor: "#fff"
            },
            notificationText: {
                marginBottom: "30px"
            },
            actionButtons: {
                position: "absolute",
                fontColor: "red",
                display: "flex",
                width: "100%",
                justifyContent: "space-between",
                right: 0,
                bottom: 0,
            },
            notificationCount: {
                display: "flex",
                position: "absolute",
                top: 0,
                right: 0,
                padding: "3px",
                width: "18px",
                height: "18px",
                border: "5px solid #db152c",
                borderRadius: "50%",
                backgroundColor: "#db152c",
                alignItems: "center",
                justifyContent: "center",
                color: "#fff",
                fontSize: "12px"
            },
            emptyList: {
                width: "100%",
                height: "50px",
                textAlign: "center",
                lineHeight: "50px"
            }
        })
);

function Notification() {
    const classes = useStyles();
    const {t} = useTranslation();
    const [isLoaded, setIsLoaded] = React.useState(false);
    const [notifications, setNotifications] = useState<INotification[]>([]);
    const [open, setOpen] = React.useState(false);
    useEffect(() => {
        if (!isLoaded) {
            requestNotifications()
                .then(n => setNotifications(n))
                .then(() => setIsLoaded(true))
        }
    });

    const notificationsToShow = useMemo(() => {
        const notificationsList: Array<any> = [];
        notifications.forEach(item => {
            if (!item.viewed) {
                notificationsList.push(<div className={classes.snackbar}>
                    <div className={classes.notificationText}>{item.text}</div>
                    <div className={classes.actionButtons}>
                        <Button color="error" size="small" style={{fontSize: "12px"}}
                                onClick={() => handleRemindLater(item.id)}>
                            {t('Remind me later')}
                        </Button>
                        |
                        <Button color="error" size="small" style={{fontSize: "12px"}}
                                onClick={() => handleDontRemind(item.id)}>
                            {t('Don\'t remind me anymore')}
                        </Button>
                    </div>
                </div>)
            }
        });
        if (notificationsList.length !==0) {
            setOpen(true);
        }
        return notificationsList;
    }, [notifications]);

    const handleNotificationClick = () => {
        setOpen(!open);
    };

    const handleClose = () => {
        setOpen(false);
    };
    const handleRemindLater = (id: string) => {
        const newNotificationList: INotification[] = notifications.filter((notification: INotification) => notification.id !== id);
        if (notificationsToShow.length === 1) {
            setOpen(false);
        }
        setNotifications(newNotificationList);
    };

    const handleDontRemind = (id: string) => {
        markReadNotification(id);
        handleRemindLater(id);
    };

    const emptyNotificationsList = <React.Fragment>
        <div className={classes.emptyList}>
            <em>{t('No notifications')}</em>
        </div>
    </React.Fragment>;

    return (
        <div className={classes.root}>
            <IconButton onClick={handleNotificationClick}><ChatIcon/></IconButton>
            <span className={classes.notificationCount}>{notificationsToShow.length}</span>
            <div
                className={classes.snackbarCase}
                style={{visibility: (open ? "visible" : "hidden")}}>
                {notificationsToShow.length !== 0 ? notificationsToShow : emptyNotificationsList}
                <div
                    className={classes.snackbarCaseClose}
                    onClick={handleClose}
                >
                    <CloseIcon fontSize="small"/>
                </div>
            </div>

        </div>


    );
}

export default Notification;
