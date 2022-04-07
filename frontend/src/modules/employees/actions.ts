import { EmployeeInterface } from "./types";
import {
  GET_EMPLOYEES_FAILURE,
  GET_EMPLOYEES_SUCCESS,
  GET_EMPLOYEE_FAILURE,
  GET_EMPLOYEE_SUCCESS,
  SET_EMPLOYEES_LOADING,
} from "./constants";
import { toast } from "react-toastify";
import createAction from "../../common/utils/createAction";
import { prepareFetchParams } from "../../store";
import { API_PREFIX } from "../../constants";
import { EmployeeFilterUIInterface } from "./types";
import * as ReferencesActions from "../../store/references/actions";
import {AdminItemType} from "../../common/types/AdminItemType";

const getEmployeesSuccess = (list: Array<EmployeeInterface>, total: number) =>
  createAction(GET_EMPLOYEES_SUCCESS, { list, total });

const getEmployeesFailure = (error: any) =>
  createAction(GET_EMPLOYEES_FAILURE, error);

const getEmployeeSuccess = (employee: EmployeeInterface) =>
  createAction(GET_EMPLOYEE_SUCCESS, employee);

const getEmployeeFailure = (error: any) =>
  createAction(GET_EMPLOYEE_FAILURE, error);

const setLoading = (isLoading: boolean) =>
  createAction(SET_EMPLOYEES_LOADING, isLoading);

const prepareFilters = (filters: EmployeeFilterUIInterface, endpoint: string | null = null) => {
  console.log('filters', filters)

  const generateSortName = (fieldName: string) => {
    if (fieldName === 'name') {
      return 'lastName';
    }
    if (fieldName === 'baseOffice') {
      return 'baseOffice.name';
    }
    if (fieldName === 'roleNames') {
      return 'roles';
    }
    if (fieldName === 'Active') {
      return 'user.active';
    }
    return fieldName;
  }

  const officeIdString = filters?.officeId ? `&officeId=${filters?.officeId}` : ``;
  const projectIdString = filters?.projectId ? `&projectId=${filters?.projectId}` : ``;
  const roleNameString = filters?.role ? `&roleName=${filters?.role}` : ``;
  const onlyActiveEmployees = `&onlyActiveEmployees=${filters?.onlyActiveEmployees}`;
  let sortParams = '';

  if (filters && filters?.sort.length > 0) {
    filters?.sort?.forEach((item, index)=> {
      sortParams += `&sorting[${index}].asc=${item['sort'] === 'asc' ? 'true' : 'false' }&sorting[0].field=${generateSortName(item['field'])}`
    })
  }
  const fetchString = `${API_PREFIX}/employee/${endpoint === 'projects' ? 'all' : 'list'}${
      filters
          ? `?pageNumber=${filters?.pageNumber ?? 0}` +
          `&pageSize=${filters?.pageSize ?? 20}` +
          encodeURI(officeIdString) +
          encodeURI(projectIdString) +
          encodeURI(onlyActiveEmployees) +
          encodeURI(roleNameString) +
          encodeURI(sortParams)+
          
          `&searchString=${filters?.searchString}`
          : ""
  }`
  return fetchString;
}
export const addEmployee = (newEmployee: EmployeeInterface, setIsAddDialogOpen: any, filters: EmployeeFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/employee`,
        prepareFetchParams({
          method: "POST",
          body: JSON.stringify(newEmployee),
        })
      );

      if (response.ok) {
        dispatch(reloadEmployees(filters, 'employees'));
        setIsAddDialogOpen(false);
        dispatch(ReferencesActions.GetReference("users"));
        toast.success(`Сотрудник создан`);
      } else {
        const message = await response.json();
        throw new Error(`${message.message}`);
      }
    } catch (error) {
      console.log({ error }.error);
      toast.error(`${{ error }.error}`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const deleteGroupEmployeesAction = (
  deletedGroup: Array<EmployeeInterface>
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
export const deleteEmployeeAction = (deletedEmployee: EmployeeInterface, filters: EmployeeFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/employee/${deletedEmployee.id}`,
        prepareFetchParams({ method: "DELETE" })
      );

      if (response.ok) {
        dispatch(reloadEmployees(filters, 'employees'));
        dispatch(ReferencesActions.GetReference("users"));
        toast.success(`Сотрудник удален`);
      } else {
        throw new Error(`Ошибка удаления ${deletedEmployee}`);
      }
    } catch (error) {
      console.log({ error });
      toast.error(`Ошибка удаления сотрудника`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};

export const editEmployeeAction = (editedEmployee: EmployeeInterface, updatedCallback: any, filters: EmployeeFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/employee/${editedEmployee.id}`,
        prepareFetchParams({
          method: "PUT",
          body: JSON.stringify(editedEmployee),
        })
      );
      if (response.ok) {
        dispatch(reloadEmployees(filters, 'employees'));
        dispatch(ReferencesActions.GetReference("users"));
        toast.success(`Сотрудник изменен`);
        updatedCallback();
        dispatch(reloadEmployees);
      } else {
        const message = await response.json();
        throw new Error(`${message.message}`);
      }
    } catch (error) {
      console.log({ error }.error);
      toast.error(`${{ error }.error}`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const getEmployee = (employeeId: string) => {
  return (dispatch: any) => {
    dispatch(setLoading(true));
    fetch(`${API_PREFIX}/employee/${employeeId}`, prepareFetchParams())
      .then((response: any) => {
        if (response.ok) {
          response
            .json()
            .then((json: any) => {
              dispatch(getEmployeeSuccess(json));
            })
            .catch((e: any) => console.error({ e }));
        } else {
          throw new Error(`Ошибка загрузки сотрудника с id:${employeeId}`);
        }
      })
      .catch((error) => {
        toast.error(`Ошибка загрузки сотрудника с id:${employeeId}`);
        console.error({ error });
        dispatch(getEmployeeFailure(error));
      })
      .finally(() => {
        dispatch(setLoading(false));
      });
  };
};
export const reloadEmployees = (
  filter: EmployeeFilterUIInterface | null = null, endpoint: string | null = null
) => {
  return async (dispatch: any, getState: any) => {
    dispatch(setLoading(true));
    try {
      const response: any = await fetch(
        filter ? prepareFilters({...filter}, endpoint) : `${API_PREFIX}/employee/${endpoint === 'projects' ? 'all' : 'list'}`,
        prepareFetchParams()
      );
      if (response.ok) {
        const json = await response.json();
        dispatch(getEmployeesSuccess(json.list, json.totalCount));
      } else {
        throw new Error("Ошибка загрузки списка сотрудников");
      }
    } catch (error) {
      toast.error("Ошибка загрузки списка сотрудников");
      console.error({ error });
      dispatch(getEmployeesFailure(error));
    } finally {
      dispatch(setLoading(false));
    }
  };
};
