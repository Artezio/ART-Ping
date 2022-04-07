
import { Action } from 'redux';

export const GET_CURRENT_USER = '@@user/GET_CURRENT_USER';
export const CURRENT_USER_RECEIVED = '@@user/CURRENT_USER_RECEIVED';
export const TRY_AUTH_USER = '@@user/TRY_AUTH_USER';
export const CURRENT_USER_LOGOUT = '@@user/CURRENT_USER_LOGOUT';
export const UPDATE_GRID_STATE = '@@user/UPDATE_GRID_STATE';
export const CHANGE_AUTH_STATUS = '@@user/CHANGE_AUTH_STATUS';
export interface UserRole {
    name: string;
    id: number;
}

export interface CurrentUserInterface {
    id: number;
    username: string;
    isFetching: Boolean;
    lang: string;
    isLoaded: boolean;
    roles: UserRole[];
    gridStates: any;
    authStatus: string;
}

export interface GetCurrentUserAction extends Action {
    type: typeof GET_CURRENT_USER;
}

export interface TryAuthUserAction extends Action {
    type: typeof TRY_AUTH_USER;
}

export interface ReceiveCurrentUserAction extends Action {
    type: typeof CURRENT_USER_RECEIVED;
    payload: {
        user: CurrentUserInterface;
    };
}

export interface UpdateGridStateAction extends Action {
    type: typeof UPDATE_GRID_STATE;
    payload: {
        gridId: string,
        newState: any
    };
}

export interface LogoutCurrentUserAction extends Action {
    type: typeof CURRENT_USER_LOGOUT;
}

export interface ChangeAuthStatusAction extends Action {
    type: typeof CHANGE_AUTH_STATUS;
    payload: {
        authStatus: string,
    }
}

export type UserActions =
    GetCurrentUserAction
    | UpdateGridStateAction
    | ReceiveCurrentUserAction
    | TryAuthUserAction
    | LogoutCurrentUserAction
    | ChangeAuthStatusAction;
