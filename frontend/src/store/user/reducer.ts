import { Reducer } from 'redux';
import {
    CurrentUserInterface,
    UserActions,

    GET_CURRENT_USER,
    UPDATE_GRID_STATE,
    CURRENT_USER_RECEIVED,
    TRY_AUTH_USER,
    CURRENT_USER_LOGOUT,
    CHANGE_AUTH_STATUS,
} from './types';

export const initialState: CurrentUserInterface = {
    id: 0,
    username: '',
    lang: 'ru',
    isFetching: false,
    isLoaded: false,
    roles: [],
    gridStates: {},
    authStatus: '',
};

export const reducer: Reducer<CurrentUserInterface> = (state: CurrentUserInterface = initialState, action) => {
    switch ((action as UserActions).type) {
        case UPDATE_GRID_STATE:
            const gridStates = { ...state.gridStates };
            gridStates[action.payload.gridId] = action.payload.newState;
            return { ...state, gridStates };
        case CURRENT_USER_RECEIVED:
            return {
                ...state,
                ...action.payload.user,
                isFetching: false,
                isLoaded: true,
            };

        case GET_CURRENT_USER:
            return {
                ...state,
                ...action.payload,
                isFetching: true
            };

        case TRY_AUTH_USER:
            return { ...state, isFetching: true };

        case CURRENT_USER_LOGOUT:
            window.location.replace("/");
            // window.location.reload();
            return null;
        case CHANGE_AUTH_STATUS:
            return {
                ...state,
                ...action.payload,
            };

        default:
            return state;
    }
};

export default reducer;
