import { Action } from "redux";
import {
  DELETE_EMPLOYEES_SUCCESS,
  GET_EMPLOYEES_FAILURE,
  GET_EMPLOYEES_SUCCESS,
  SET_EMPLOYEES_LOADING,
  DELETE_GROUP_EMPLOYEES_SUCCESS,
} from "./constants";

export interface State {
  ui: {
    isLoading: boolean;
  };
  data: {
    list: Array<EmployeeInterface>;
    total: number;
  };
}

export interface EmployeeBaseInterface {
  id?: string;
  calendarId: string;
  officeId: string;
  firstName: string;
  lastName: string;
  login: string;
}

export interface EmployeeInterface extends EmployeeBaseInterface {
  email?: string;
  password: string;
  active?: true;
}

export interface GetEmployeesFailureAction extends Action {
  type: typeof GET_EMPLOYEES_FAILURE;
  payload: any;
}

export interface GetEmployeesSuccessAction extends Action {
  type: typeof GET_EMPLOYEES_SUCCESS;
  payload: Array<EmployeeInterface>;
}

export interface SetEmployeesLoadingAction extends Action {
  type: typeof SET_EMPLOYEES_LOADING;
  payload: boolean;
}

export interface DeleteEmployeeAction extends Action {
  type: typeof DELETE_EMPLOYEES_SUCCESS;
  payload?: Array<EmployeeInterface>;
}

export interface DeleteGroupEmployeesAction extends Action {
  type: typeof DELETE_GROUP_EMPLOYEES_SUCCESS;
  payload?: Array<EmployeeInterface>;
}

export type ListActions =
  | GetEmployeesFailureAction
  | GetEmployeesSuccessAction
  | SetEmployeesLoadingAction
  | DeleteEmployeeAction
  | DeleteGroupEmployeesAction;

export enum Type {
  EMPLOYEES = "employees",
}
export interface EmployeeFilterUIInterface {
  officeId: string | null;
  projectId: string | null;
  searchString: string;
  pageNumber: number;
  pageSize: number;
  role: string | null;
  sort: [];
  onlyActiveEmployees: boolean;
}
