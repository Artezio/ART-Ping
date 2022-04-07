import { useDispatch } from 'react-redux';
import * as NotificationsActions from '../../store/notifications/actions';
import { NotificationSetupDialog } from './NotificationSetupDialog'

export const NotificationConnect = (props: any) => {
    const dispatch = useDispatch();

    if (!("Notification" in window)) {
        alert("This browser does not support desktop notification");
    }

    switch (Notification.permission) {
        case "granted":
            dispatch(NotificationsActions.InitNotifications());
            break;
        default:
            return <NotificationSetupDialog />
    }

    return <>{props.children}</>
}