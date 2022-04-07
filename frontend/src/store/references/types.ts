import { Action } from 'redux';
export const GET_REFERENCES_LIST = '@@references/GET_REFERENCES_LIST';
export const REFERENCE_RECEIVED = '@@references/REFERENCE_RECEIVED';
export const REFERENCE_BATH_RECEIVED = '@@references/REFERENCE_BATH_RECEIVED';
export const GET_LIST = '@@references/GET_LIST';
export const UPDATE_FILTERS = '@@references/UPDATE_FILTERS';

export interface ListItemInterface {
    isFetching: boolean;
    list: any;
    filtersState: any
}
export interface ListInterface {
    isFetching: boolean,
    isLoaded: boolean,
    refs: any
}

export interface GetListAction extends Action {
    type: typeof GET_LIST;
}
export interface ReferenceReceivedAction extends Action {
    type: typeof REFERENCE_RECEIVED;
}
export interface ReferenceBathReceivedAction extends Action {
    type: typeof REFERENCE_BATH_RECEIVED;
}
export interface GetReferenceAction extends Action {
    type: typeof GET_REFERENCES_LIST;
}
export interface UpdateFiltersAction extends Action {
    type: typeof UPDATE_FILTERS;
    payload: {
        newState: any;
    };
}

export type ListActions = GetListAction | ReferenceReceivedAction | ReferenceBathReceivedAction| GetReferenceAction | UpdateFiltersAction;
