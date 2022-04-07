import React, {useState, useEffect} from 'react';
import Typography from '@material-ui/core/Typography';
import {useDispatch} from 'react-redux';
import {TypedSelector} from '../../../store';
import * as NotificationsActions from '../../../store/notifications/actions';
import {useTranslation} from "react-i18next";
import * as Icons from '@material-ui/icons';
import Button from '@material-ui/core/Button';
import {useHistory} from 'react-router';
import Reaptcha from 'reaptcha';
import {requestPic} from "../../../store/system/actions";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme): {
        root: any,
        image: any,
        wishText: any
    } =>
        createStyles({
            root: {
                height: "calc(100% - 64px)",
                padding: "50px",
                display: "flex",
                justifyContent: "space-between"
            },
            image: {
                maxWidth: '70vw',
                height: "auto"
            },
            wishText: {
                paddingTop: "100px",
                fontSize: "48px",
                fontWeight: "bold"
            }
        })
)

const UserPingPanel = () => {
    const classes = useStyles();
    const notifications = TypedSelector(state => state.notifications);
    const dispatch = useDispatch();
    const {t} = useTranslation();
    const history = useHistory();
    const [verified, setVerified] = useState(false);
    const [imageUrl, setImageUrl] = useState('');
    useEffect(() => {
        if (!imageUrl) {
            requestPic().then(n=>setImageUrl(n))
        }
    });
    const imHereHandler = () => {
        dispatch(NotificationsActions.OnPingAccepted(notifications?.test?.id));
        history.push('/');
    }

    const mcps_str = t('Here you ping checks')

    let display = <></>;

    const onVerify = (recaptchaResponse: any) => {
        setVerified(true);
    }

    const getButton = () => <>
        <Reaptcha sitekey="6Les5R8dAAAAAADekp7cEf8B8umJxOXOuAO1ojZo" onVerify={onVerify}/>
        <Button
            disabled={!verified}
            color="primary"
            title={`${t("I`m here")}!!!!!`}
            onClick={imHereHandler}
        >
            <Icons.ThumbUp/> {t('I`m here!')}
        </Button>
    </>;

    switch (notifications?.test?.status) {
        case NotificationsActions.PING_STATUS.IN_PROGRESS:
            display = getButton();
            break;
        case NotificationsActions.PING_STATUS.NOT_SUCCESS:
            display = <Typography variant="h5">{mcps_str}: {t('FAILED')}</Typography>
            break;

        case NotificationsActions.PING_STATUS.SUCCESS:
            display = <Typography variant="h5">M{mcps_str}: {t('SUCCESS')}</Typography>
            break;

        default:
            if (new URLSearchParams(window.location.search).get("ping")) {
                display = getButton();
            } else {
                display = <Typography variant="h5">{mcps_str}</Typography>
            }
            break;
    }

    return (
        <div className={classes.root}>
            <div>
                {display}
                <h1 className={classes.wishText}>Хорошего дня!</h1>
            </div>
            <img className={classes.image} src={imageUrl}/>
    </div>);
}

export default UserPingPanel;
