
//import parseJWT from '../../common/utils/parseJWT'
import { toast } from "react-toastify";
//import DefaultUser from '../../dummy/DefaultUser';
import { prepareFetchParams } from '../shared';
import * as FirebaseServiceActions from '../notifications/service_firebase';
import { ActionCreator } from 'redux';
import {
    CurrentUserInterface,
    ReceiveCurrentUserAction,
    LogoutCurrentUserAction,
    UpdateGridStateAction,
    ChangeAuthStatusAction,
    UPDATE_GRID_STATE,
    GET_CURRENT_USER,
    CURRENT_USER_RECEIVED,
    TRY_AUTH_USER,
    CURRENT_USER_LOGOUT,
    CHANGE_AUTH_STATUS,
} from './types';
import { API_PREFIX, API_URL, NOTIFICATION_TRANSPORT } from '../../constants';
import {
    FIREBASE_LOCALSTORAGE_TOKEN_KEY,
} from '../../constants/firebase_config';




const requestCurrentUser = (
    dispatch: any
) => {
    fetch(`${API_PREFIX}/user/whoami`, prepareFetchParams())
        .then(response => {
            response
                .json()
                .then((json: any) => {
                    if (response.status !== 200) {
                        toast.warn(`Auth failure`);
                        console.error({ point: 'requestCurrentUser:response.status !== 200', json });
                        requestToLogout(dispatch);
                    } else {
                        dispatch(ReceiveCurrentUser(json));
                    }
                })
                .catch(error => {
                    toast.warn(`Auth failure`);
                    console.error({ point: 'requestCurrentUser:jsonParse', error });
                    requestToLogout(dispatch);
                });
        })
        .catch(error => {
            toast.warn(`Auth failure`);
            requestToLogout(dispatch);
            console.error({ point: 'requestCurrentUser:fetch', error });
        });

    return {
        type: GET_CURRENT_USER
    };
}


const requestToAuth = (
    dispatch: any,
    credentials: any
) => {
    const formData = new FormData();
    for (let key in credentials) {
        formData.append(key, credentials[key]);
    }
    fetch(`${API_PREFIX}/authenticate`, prepareFetchParams({ method: 'POST', body: JSON.stringify(credentials) }))
        .then(response => {
            response
                .json()
                .then((json: any) => {
                    if (response.status !== 200) {
                        console.error({ point: 'requestToAuth:response.status !== 200', json });
                        dispatch(ChangeAuthStatus(json?.message));
                    } else {
                        localStorage.setItem('jwt', json.jwt)
                        localStorage.setItem('rememberMe', credentials.rememberMe)
                        toast.success(`Аутентификация пройдена`, { pauseOnFocusLoss: false });
                        dispatch(requestCurrentUser(dispatch));
                    }
                })
                .catch(error => {
                    toast.error(`Auth failure: ${error}`, { pauseOnFocusLoss: false });
                    console.error({ point: 'requestToAuth:parse json', error });
                });
        })
        .catch(error => {
            toast.error(`Auth failure: ${error}`, { pauseOnFocusLoss: false });
            console.error({ point: 'requestToAuth:fetch:error', error });
        });
}

const requestToLogout = (
    dispatch: any
) => {
    if (NOTIFICATION_TRANSPORT === 'firebase') {
        const currentFirebaseToken = FirebaseServiceActions.tokenStorage.get();
        FirebaseServiceActions.updateSubscription(currentFirebaseToken, FirebaseServiceActions.TOKEN_ACTIONS.DELETE);
        localStorage.removeItem(FIREBASE_LOCALSTORAGE_TOKEN_KEY);
    }
    fetch(API_PREFIX + 'asp-net-identity/LogOff', prepareFetchParams())
        .then(() => {
            localStorage.removeItem('jwt');
            dispatch(LogoutCurrentUser());
        })
        .catch(error => { console.error({ point: 'requestToLogout', error }); });
}

export const GetCurrentUser = () => {
    return (dispatch: any) => {
        requestCurrentUser(dispatch);
        return {
            type: GET_CURRENT_USER
        }
    }
};

export const ReceiveCurrentUser: ActionCreator<ReceiveCurrentUserAction> = (user: CurrentUserInterface) => {
    return {
        type: CURRENT_USER_RECEIVED,
        payload: {
            user
        },
    };
}

export const ChangeAuthStatus: ActionCreator<ChangeAuthStatusAction> = (authStatus: string) => {
    return {
        type: CHANGE_AUTH_STATUS,
        payload: {
            authStatus
        },
    };
}

export const UpdateGridState: ActionCreator<UpdateGridStateAction> = (gridId: string, newState: any) => {
    return {
        type: UPDATE_GRID_STATE,
        payload: {
            gridId,
            newState
        }
    };
}

export const LogoutCurrentUser: ActionCreator<LogoutCurrentUserAction> = () => {
    return {
        type: CURRENT_USER_LOGOUT
    };
}

export const TryAuthUser = (credentials: any) => {
    return (dispatch: any) => {
        requestToAuth(dispatch, credentials);
        return {
            type: TRY_AUTH_USER
        };
    }
};

export const TryLogoutUser = () => {
    return (dispatch: any) => {
        requestToLogout(dispatch);
        return {
            type: TRY_AUTH_USER
        };
    }
};

export const isNeedRememberUser = () => {
    if (localStorage.getItem('rememberMe') === 'false') {
        localStorage.removeItem('jwt');
    }
}

// export const mapStateToProps = (state, params) => {
//     const { legacyProjectId, match: { params: { projectId } = {} } = {} } =
//     params || {};
//     return {
//         projectType: selectors.getProjectType(state, legacyProjectId || projectId),
//         loaded: selectors.isLoaded(state),
//         projects: selectors.getAllProjects(state),
//         currentProject: projectSelectors.getCurrentProject(state),
//         projectCurrentUser: projectSelectors.getProjectCurrentUser(state),
//         counts: projectSelectors.getCurrentProjectTabCounts(state),
//         user: authSelectors.currentUser(state),
//     };
// };
