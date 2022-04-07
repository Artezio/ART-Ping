import {Action} from "redux";
import {DELETE_ITEM_SUCCESS, GET_ITEMS_FAILURE, GET_ITEMS_SUCCESS, SET_ITEMS_LOADING} from "./constants";
import {date} from "yup";

export interface DataInterface {
    list: Array<ProjectInterface | OfficeInterface | CalendarInterface | UserInterface>,
    isLoading: boolean,
}

export enum Type {
    USERS = "users",
    PROJECTS = "projects",
    OFFICES = "offices",
    CALENDARS = "calendars",

}

export enum EditorMode {
    EDIT = "edit",
    CREATE = "create",
}

export interface State {
    data: {
        [key: string]: DataInterface,
    }
}


export interface GetFailureAction extends Action {
    type: typeof GET_ITEMS_FAILURE;
    payload: {
        type: Type
        error: Array<ProjectInterface | OfficeInterface | CalendarInterface | UserInterface>;
    };
}

export interface GetSuccessAction extends Action {
    type: typeof GET_ITEMS_SUCCESS;
    payload: {
        type: Type
        list: Array<ProjectInterface | OfficeInterface | CalendarInterface | UserInterface>;
    };
}


export interface SetLoadingAction extends Action {
    type: typeof SET_ITEMS_LOADING;
    payload: {
        type: Type
        isLoading: boolean;
    };
}

export interface DeleteItemAction extends Action {
    type: typeof DELETE_ITEM_SUCCESS;
    payload: {
        type: Type
        deletedItem: Array<ProjectInterface | OfficeInterface | CalendarInterface | UserInterface>;
    }
}


export type ListActions =
    | GetFailureAction
    | GetSuccessAction
    | SetLoadingAction
    | DeleteItemAction;

export enum VacationDayType {
    PAID = "PAID",
}

export interface VacationDay {
    name: string,
    start: string,
    end: string,
    type: VacationDayType
}

export interface CalendarInterface {
    id?: string,
    name: string,
    vacationDays: Array<VacationDay>,
    officeId?: string,
    startMonth?: number,
}



export interface OfficeInterface {
    id?: string,
    name: string,
    calendarId?: string,
    timezone: string,
    vacationDays: Array<VacationDay>,
}


export interface UserInterface {
    id?: string,
    login: string,
    password: string
}

export interface ProjectInterface {
    id?: string,
    name: string,
    active: true,
}