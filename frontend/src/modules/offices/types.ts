import {Action} from "redux";
import {
    DELETE_GROUP_OFFICES_SUCCESS,
    DELETE_OFFICES_SUCCESS,
    GET_OFFICES_FAILURE,
    GET_OFFICES_SUCCESS,
    SET_OFFICES_LOADING,
} from "./constants";

export interface State {
    ui: {
        isLoading: boolean;
    };
    data: {
        list: Array<OfficeInterface>;
        total: number;
    };
}

export interface OfficeInterface {
    id?: string;
    name: string;
    active?: boolean;
    calendarId: string;
    timezone: string;
}


export interface GetOfficesFailureAction extends Action {
    type: typeof GET_OFFICES_FAILURE;
    payload: any;
}

export interface GetOfficesSuccessAction extends Action {
    type: typeof GET_OFFICES_SUCCESS;
    payload: Array<OfficeInterface>;
}

export interface SetOfficesLoadingAction extends Action {
    type: typeof SET_OFFICES_LOADING;
    payload: boolean;
}

export interface DeleteOfficeAction extends Action {
    type: typeof DELETE_OFFICES_SUCCESS;
    payload?: Array<OfficeInterface>;
}

export interface DeleteGroupOfficesAction extends Action {
    type: typeof DELETE_GROUP_OFFICES_SUCCESS;
    payload?: Array<OfficeInterface>;
}

export interface OfficeFilterUIInterface {
    searchString: string;
    pageSize: number;
    pageNumber: number;
    sort: [];
}

export type ListActions =
    | GetOfficesFailureAction
    | GetOfficesSuccessAction
    | SetOfficesLoadingAction
    | DeleteOfficeAction
    | DeleteGroupOfficesAction;
