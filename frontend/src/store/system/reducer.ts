import {Reducer} from 'redux';
import {
    ListInterface,
    ListActions,

    UPDATE_FILTERS,
    LIST_RECEIVED,
    GET_LIST,
    GET_APP_INFO,
    APP_INFO_RECEIVED,
} from './types';

export const initialState: ListInterface = {
    ui: {
        isLoading: false,
    },
    isFetching: false,
    isLoaded: false,
    list: [],
    appInfo: null,
    filtersState: {},
};

export const reducer: Reducer<ListInterface> = (state: ListInterface = initialState, action) => {
    switch ((action as ListActions).type) {
        case UPDATE_FILTERS:
            return {
                ...state,
                filtersState: action.payload.newState
            };

        case LIST_RECEIVED:
            return {
                ...state,
                isLoaded: true,
                list: action.payload || [],
                isFetching: false
            };

        case GET_LIST:
            return {...state, isFetching: true};


        case GET_APP_INFO:
            return {...state, isFetching: true};

        case APP_INFO_RECEIVED:
            return {
                ...state,
                appInfo: action.payload || {},
                isFetching: false
            };

        default:
            return state;
    }
};

export default reducer;
