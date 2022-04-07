import { Action } from "redux";
import {
  DELETE_CALENDARS_SUCCESS,
  GET_CALENDARS_FAILURE,
  GET_CALENDARS_SUCCESS,
  SET_CALENDARS_LOADING,
  DELETE_GROUP_CALENDARS_SUCCESS,
} from "./constants";

export interface State {
  ui: {
    isLoading: boolean;
  };
  data: {
    list: Array<CalendarInterface>;
    total: number;
  };
}

export interface CalendarInterface {
  id?: string;
  name: string;
  active?: boolean;
  office?: string;
  endDate?: string;
  startDate?: string;
  creationTime?: string;
}

export interface GetCalendarsFailureAction extends Action {
  type: typeof GET_CALENDARS_FAILURE;
  payload: any;
}

export interface GetCalendarsSuccessAction extends Action {
  type: typeof GET_CALENDARS_SUCCESS;
  payload: Array<CalendarInterface>;
}

export interface SetCalendarsLoadingAction extends Action {
  type: typeof SET_CALENDARS_LOADING;
  payload: boolean;
}

export interface DeleteCalendarsAction extends Action {
  type: typeof DELETE_CALENDARS_SUCCESS;
  payload?: Array<CalendarInterface>;
}
export interface DeleteGroupCalendarsAction extends Action {
  type: typeof DELETE_GROUP_CALENDARS_SUCCESS;
  payload?: Array<CalendarInterface>;
}
export type ListActions =
  | GetCalendarsFailureAction
  | GetCalendarsSuccessAction
  | SetCalendarsLoadingAction
  | DeleteCalendarsAction
  | DeleteGroupCalendarsAction;

export interface CalendarFilterUIInterface {
  pageNumber: number;
  pageSize: number;
  searchString: string;
  sort: [];
}
