import { Action } from 'redux';

export const UPDATE_FILTERS = '@@system/UPDATE_FILTERS';
export const LIST_RECEIVED = '@@system/LIST_RECEIVED';
export const GET_LIST = '@@employeeTests/GET_LIST';

export const GET_APP_INFO = '@@employeeTests/GET_APP_INFO';
export const APP_INFO_RECEIVED = '@@employeeTests/APP_INFO_RECEIVED';

export interface ListItemInterface {
    id: string | number;
    name: string;
    type: string;
    title?: string;
    value?: any;
    isCustomizable?: boolean;
    description?: string;
}

export interface ListInterface {
    ui: {
        isLoading: boolean;
    };
    isFetching: boolean;
    isLoaded: boolean;
    list: ListItemInterface[];
    appInfo: any;
    filtersState: any;
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

export interface GetAppInfoAction extends Action {
    type: typeof GET_APP_INFO;
}

export interface AppInfoReceivedAction extends Action {
    type: typeof APP_INFO_RECEIVED;
}



export type ListActions = GetListAction | ListReceivedAction | UpdateFiltersAction | GetAppInfoAction | AppInfoReceivedAction;
