import { Reducer } from 'redux';
import preloadList from './preload';
import { DEFAULT_EMPLOYEE_TESTS_FILTER_STATE } from '../../constants';
import {
    ListInterface,
    ListActions,
    GET_REFERENCES_LIST,
    REFERENCE_RECEIVED,
    GET_LIST,
    REFERENCE_BATH_RECEIVED,
    UPDATE_FILTERS
} from './types';

export const initialState: ListInterface = {
    isFetching: false,
    isLoaded: false,
    refs: {}
};

export const reducer: Reducer<ListInterface> = (state: ListInterface = initialState, action) => {
    let newState = Object.assign({}, state);
    switch ((action as ListActions).type) {
        case GET_REFERENCES_LIST:
            newState.isFetching = true;
            return newState;
        case UPDATE_FILTERS:
            newState.refs[action.payload.name].filtersState = action.payload.newFilterState
            return newState;

        case REFERENCE_RECEIVED:
            if (action.payload.name && !newState.refs[action.payload.name]) {
                newState.refs[action.payload.name] = {};
            }

            if (newState.refs[action.payload.name]) {
                newState.refs[action.payload.name].list = action.payload.list
            }

            if (JSON.stringify(Object.keys(newState.refs)) === JSON.stringify(Object.keys(preloadList))) {
                newState.isLoaded = true;
            }

            return newState;
        case REFERENCE_BATH_RECEIVED:
            newState.refs = action.payload;
            newState.isLoaded = true;
            newState.isFetching = false;
            return newState;
        case GET_LIST:
            return state;

        default:
            return state;
    }
};

export default reducer;
