import { Action } from "redux";
import {
  DELETE_PROJECTS_SUCCESS,
  GET_PROJECTS_FAILURE,
  GET_PROJECTS_SUCCESS,
  SET_PROJECTS_LOADING,
  DELETE_GROUP_PROJECTS_SUCCESS,
} from "./constants";

export interface State {
  ui: {
    isLoading: boolean;
  };
  data: {
    list: Array<ProjectInterface>;
    total: number;
  };
}

export interface ProjectInterface {
  id?: string;
  name: string;
  active?: true;
}

export interface GetProjectsFailureAction extends Action {
  type: typeof GET_PROJECTS_FAILURE;
  payload: any;
}

export interface GetProjectsSuccessAction extends Action {
  type: typeof GET_PROJECTS_SUCCESS;
  payload: Array<ProjectInterface>;
}

export interface SetProjectsLoadingAction extends Action {
  type: typeof SET_PROJECTS_LOADING;
  payload: boolean;
}

export interface DeleteProjectAction extends Action {
  type: typeof DELETE_PROJECTS_SUCCESS;
  payload?: Array<ProjectInterface>;
}
export interface DeleteGroupProjectsAction extends Action {
  type: typeof DELETE_GROUP_PROJECTS_SUCCESS;
  payload?: Array<ProjectInterface>;
}

export interface ProjectFilterUIInterface {
  searchString: string;
  pageSize: number;
  pageNumber: number;
  sort: [];
}

export type ListActions =
  | GetProjectsFailureAction
  | GetProjectsSuccessAction
  | SetProjectsLoadingAction
  | DeleteProjectAction
  | DeleteGroupProjectsAction;
