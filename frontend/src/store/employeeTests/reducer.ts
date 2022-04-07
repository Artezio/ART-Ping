import { Reducer } from 'redux';
import { DEFAULT_EMPLOYEE_TESTS_FILTER_STATE } from '../../constants';
import {
    ListInterface,
    ListActions,

    UPDATE_FILTERS,
    LIST_RECEIVED,
    GET_LIST,
} from './types';

export const initialState: ListInterface = {
    isFetching: false,
    list: [],
    filtersState: DEFAULT_EMPLOYEE_TESTS_FILTER_STATE
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
                list:action.payload.list,
                isFetching: false
            };

        case GET_LIST:
            return { ...state, isFetching: true };

        default:
            return state;
    }
};

export default reducer;