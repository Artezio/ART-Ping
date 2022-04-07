import { Action } from 'redux';
    export const UPDATE_FILTERS = '@@employeeTests/GET_CURRENT_USER';
    export const LIST_RECEIVED = '@@employeeTests/LIST_RECEIVED';
    export const GET_LIST = '@@employeeTests/GET_LIST';

    
export interface ListItemInterface {
    name: string;
    id: number;
}
export interface ListInterface{
    isFetching: boolean;
    list: ListItemInterface[];
    filtersState: any
};

export interface GetListAction extends Action {
    type: typeof GET_LIST;
}
export interface ListReceivedAction extends Action {
    type: typeof LIST_RECEIVED;
}

export interface UpdateFiltersAction extends Action {
    type: typeof UPDATE_FILTERS;
    payload: {
        newState: any;
    };
}

export type ListActions = GetListAction | ListReceivedAction | UpdateFiltersAction;
