import {Reducer} from 'redux';
import {ListActions, State} from './types';
import {GET_ITEMS_FAILURE, GET_ITEMS_SUCCESS, SET_ITEMS_LOADING,} from "./constants";

export const initialState: State = {
    data: {
        projects: {
            list: [],
            isLoading: false
        },
        offices: {
            list: [],
            isLoading: false
        },
        users: {
            list: [],
            isLoading: false
        },
        system: {
            list: [],
            isLoading: false
        },
        calendars: {
            list:[],
            isLoading: false
        }
    }
};

export const reducer: Reducer<State> = (state: State = initialState, action) => {
    switch ((action as ListActions).type) {

        case SET_ITEMS_LOADING:
            return {
                ...state,
                data: {
                    ...state.data,
                    [action.payload.type]: {
                        ...state.data[action.payload.type],
                        isLoading: action.payload.isLoading
                    }
                }
            };
        case GET_ITEMS_SUCCESS:
            return {
                ...state,
                data: {
                    ...state.data,
                    [action.payload.type]: {
                        list: action.payload.list,
                        isLoading: false
                    }
                }
            };
        case GET_ITEMS_FAILURE:
            return {
                ...state,
                data: {
                    ...state.data,
                    [action.payload.type]: {
                        list: [],
                        isLoading: false
                    }
                }
            };


        default:
            return state;
    }
};

export default reducer;