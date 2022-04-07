import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { TypedSelector } from './store';
import * as UserActions from './store/user/actions';
import * as SystemActions from './store/system/actions';
import * as ReferencesActions from './store/references/actions';
import * as NotificationsActions from './store/notifications/actions';
import MainContent from './components/MainContent/MainContent';
import AppLoadingScreen from './components/AppLoadingScreen/AppLoadingScreen';
import AuthForm from './components/AuthForm/AuthForm';
import './App.css';
import { ToastContainer } from 'react-toastify';
import { DialogConfirm } from './common/dialogs/DialogConfirm';
import { useTranslation } from "react-i18next";

const url = "/?ping=1";

const App = () => {
    const dispatch = useDispatch();
    const { t } = useTranslation();
    const history = useHistory();
    const currentUser = TypedSelector(state => state.user);
    const currentSettings = TypedSelector(state => state.system);
    const currentReferences = TypedSelector(state => state.references);
    const isConfirmPingOpen = TypedSelector(state => state.notifications.isConfirmPingOpen);
    const checkUrl = TypedSelector(state => state.notifications.url);

    const jwt: string | null = localStorage.getItem('jwt') || null;
    let content = <AppLoadingScreen />;
    // try to load app info from backend
    if (!currentSettings.appInfo && !currentSettings.isFetching) {
        dispatch(SystemActions.loadAppInfo());
    }

    // try to load user with token
    if (jwt && currentUser && !currentUser.isFetching && !currentUser.login) {
        dispatch(UserActions.GetCurrentUser());
        //2.1 if loaded - try to load app
    }

    // if failed - logout and go to auth screen
    if (!jwt && currentUser && !currentUser.isFetching) {
        content = <AuthForm />
    }

    // try to load app
    if (currentUser && !currentUser.isFetching && currentUser.login) {
        // try to load app and user settings & preload references
        // get settings
        content = <AppLoadingScreen />;
        if (!currentSettings.isFetching && !currentSettings.isLoaded) {
            dispatch(SystemActions.getList());
        }

        // get refetences
        if (currentReferences && !currentReferences.isFetching && !currentReferences.isLoaded) {
                dispatch(ReferencesActions.GetReferencesList());
        }
        // go to main screen
        if (currentReferences && !currentReferences.isFetching && currentReferences.isLoaded) {
            content = <MainContent />
        }
    }

    useEffect(() => {
        window.addEventListener('beforeunload', UserActions.isNeedRememberUser);
        return () => {
            window.removeEventListener('beforeunload', UserActions.isNeedRememberUser)
        }
    }, [])
    return <>
            {content}
            <DialogConfirm
                open={isConfirmPingOpen}
                t={('Open the page?')}
                onYesClick={() => {
                    dispatch(NotificationsActions.pingToastClicked({open:false}));
                    history.push(checkUrl || url);
                }}
                onNoClick={() => {
                    dispatch(NotificationsActions.pingToastClicked({open:false}));
                }}/>
            <ToastContainer />
        </>
}

export default App;
