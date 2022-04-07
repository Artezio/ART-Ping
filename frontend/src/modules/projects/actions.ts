import { ProjectFilterUIInterface, ProjectInterface } from "./types";
import {
  GET_PROJECTS_FAILURE,
  GET_PROJECTS_SUCCESS,
  SET_PROJECTS_LOADING,
} from "./constants";
import { toast } from "react-toastify";
import createAction from "../../common/utils/createAction";
import { AdminItemType } from "../../common/types/AdminItemType";
import { prepareFetchParams } from "../../store";
import { API_PREFIX } from "../../constants";
import * as ReferencesActions from "../../store/references/actions";

function getProjectsSuccess(list: Array<ProjectInterface>, total: number) {
  return createAction(GET_PROJECTS_SUCCESS, { list, total });
}

const prepareFilters = (filters: ProjectFilterUIInterface) => {
  let sortParams = '';

  if (filters && filters?.sort.length > 0) {
    filters?.sort?.forEach((item, index)=> {
      sortParams += `&sorting[${index}].asc=${item['sort'] === 'asc' ? 'true' : 'false' }&sorting[0].field=${item['field']}`
    })
  }

  const fetchString = `${API_PREFIX}/${AdminItemType.PROJECTS}${
      filters
          ? `?pageNumber=${filters?.pageNumber ?? 0}` +
          `&pageSize=${filters?.pageSize ?? 20}` +
          encodeURI(sortParams)+
          `&searchString=${filters?.searchString}`
          : ""
  }`
  return fetchString;
}

const getProjectsFailure = (error: any) =>
  createAction(GET_PROJECTS_FAILURE, error);

const setLoading = (isLoading: boolean) =>
  createAction(SET_PROJECTS_LOADING, isLoading);

export const addProject = (newProject: ProjectInterface, filters: ProjectFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/${AdminItemType.PROJECTS}`,
        prepareFetchParams({
          method: "POST",
          body: JSON.stringify(newProject),
        })
      );
      const message = await response.json();
      if (response.ok) {
        dispatch(reloadProjects(filters));
        dispatch(ReferencesActions.GetReference('projects'));
        dispatch(ReferencesActions.GetReference('users'));
        toast.success(`Проект создан`);
      } else {
        throw new Error(
            message.message || `Ошибка создания проекта`
        );
      }
    } catch (error) {
      console.log({ error });
      dispatch(reloadProjects());
      toast.error(`${{ error }.error}` || `Project not created`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const deleteGroupProjectsAction = (
  deletedProjects: Array<ProjectInterface>
) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      throw new Error(`Not implemented yet`);
    } catch (error) {
      console.log({ error });
      toast.error(`Not implemented yet`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const deleteProjectAction = (deletedProject: ProjectInterface, filters: ProjectFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/${AdminItemType.PROJECTS}/${deletedProject.id}`,
        prepareFetchParams({
          method: "DELETE",
        })
      );

      if (response.ok) {
        dispatch(reloadProjects(filters));
        dispatch(ReferencesActions.GetReference('projects'));
        dispatch(ReferencesActions.GetReference('users'));
        toast.success(`Проект удален`);
      } else {
        throw new Error(`Ошибка удаления проекта ${deletedProject}`);
      }
    } catch (error) {
      console.log({ error });
      toast.error(`Ошибка удаления проекта`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const editProjectAction = (editedProject: ProjectInterface, updatedCallback: any, filtersState: ProjectFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/${AdminItemType.PROJECTS}`,
        prepareFetchParams({
          method: "PUT",
          body: JSON.stringify(editedProject),
        })
      );
      const message = await response.json();
      if (response.ok) {
        dispatch(reloadProjects(filtersState));
        dispatch(ReferencesActions.GetReference('projects'));
        dispatch(ReferencesActions.GetReference('users'));
        toast.success(`Проект отредактирован`);
        updatedCallback();
      } else {
        throw new Error(
            message.message || `Ошибка редактирования проекта`
        );
      }
    } catch (error) {
      console.log({ error });
      toast.error(`${{ error }.error}` || `Проект не отредактирован`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const reloadProjects = (
  filter: ProjectFilterUIInterface | null = null
) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response: any = await fetch(
          filter ? prepareFilters({...filter}) : `${API_PREFIX}/${AdminItemType.PROJECTS}`,
          prepareFetchParams()
      );
      if (response.ok) {
        const json = await response.json();
        dispatch(getProjectsSuccess(json.list, json.totalCount));
      } else {
        throw new Error(`Ошибка загрузки `);
      }
    } catch (error) {
      toast.error(`Ошибка загрузки списка проектов`);
      console.error({ error });
      dispatch(getProjectsFailure(error));
    } finally {
      dispatch(setLoading(false));
    }
  };
};
