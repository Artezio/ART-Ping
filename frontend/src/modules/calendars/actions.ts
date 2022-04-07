import { CalendarFilterUIInterface, CalendarInterface } from "./types";
import {
  GET_CALENDARS_FAILURE,
  GET_CALENDARS_SUCCESS,
  SET_CALENDARS_LOADING,
} from "./constants";
import { toast } from "react-toastify";
import createAction from "../../common/utils/createAction";
import { AdminItemType } from "../../common/types/AdminItemType";
import { prepareFetchParams } from "../../store";
import { API_PREFIX } from "../../constants";
import * as ReferencesActions from "../../store/references/actions";

function getCalendarsSuccess(list: Array<CalendarInterface>, total: number) {
  return createAction(GET_CALENDARS_SUCCESS, { list, total });
}

const prepareFilters = (filters: CalendarFilterUIInterface) => {
  let sortParams = '';
  if (filters && filters?.sort.length > 0) {
    filters?.sort?.forEach((item, index)=> {
      sortParams += `&sorting[${index}].asc=${item['sort'] === 'asc' ? 'true' : 'false' }&sorting[0].field=${item['field']}`
    })
  }
  const fetchString = `${API_PREFIX}/${AdminItemType.CALENDARS}${
      filters
          ? `?pageNumber=${filters?.pageNumber ?? 0}` +
          `&pageSize=${filters?.pageSize ?? 20}` +
          encodeURI(sortParams)+
          `&searchString=${filters?.searchString}`
          : ""
  }`
  return fetchString;
}

const getCalendarsFailure = (error: any) =>
  createAction(GET_CALENDARS_FAILURE, error);

const setLoading = (isLoading: boolean) =>
  createAction(SET_CALENDARS_LOADING, isLoading);

export const addCalendar = (newCalendar: CalendarInterface, filters: CalendarFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    const requestOpts: RequestInit = prepareFetchParams({
      method: "POST",
      body: JSON.stringify(newCalendar),
    });
    try {
      const response = await fetch(
          `${API_PREFIX}/${AdminItemType.CALENDARS}`, requestOpts);
      const message = await response.json();
      if (response.ok) {
        dispatch(reloadCalendars(filters));
        dispatch(ReferencesActions.GetReference('calendars'));
        toast.success(`Календарь создан`);
      } else {
        throw new Error(
            message.message || `Cannot create calendar`
        );
      }
    }
    catch (error) {
      console.error("addCalendar", {error});
      dispatch(reloadCalendars());
      toast.error(`${{ error }.error}` || `Календарь не создан`);
    }
    finally {
        dispatch(setLoading(false));
    };
  };
};
export const deleteGroupCalendarsAction = (
  deletedCalendars: Array<CalendarInterface>
) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));


    setTimeout(() => {
      dispatch(setLoading(false));
    }, 1000);
  };
};
export const deleteCalendarsAction = (deletedCalendars: CalendarInterface, filters: CalendarFilterUIInterface) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/${AdminItemType.CALENDARS}/${deletedCalendars.id}`,
        prepareFetchParams({ method: "DELETE" })
      );
      const json = await response.json();
      if (response.ok) {
        dispatch(reloadCalendars(filters));
        dispatch(ReferencesActions.GetReference('calendars'));
        toast.success(`Календарь удален`);
      } else {
        console.error({ point: "deleteCalendarsAction.catch[1]", response });
        throw new Error(
          json.message || `Cannot delete ${deletedCalendars.name}`
        );
      }
    } catch (error: any) {
      toast.error(error.message || `Ошибка при удалении календаря`);
      console.error({ point: "deleteCalendarsAction.catch[2]", error });
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const editCalendarsAction = (
  editedCalendars: CalendarInterface,
  updatedCallback: any,
  filters: CalendarFilterUIInterface
) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response = await fetch(
        `${API_PREFIX}/${AdminItemType.CALENDARS}/${editedCalendars.id}`,
        prepareFetchParams({
          method: "PUT",
          body: JSON.stringify(editedCalendars),
        })
      );
      const message = await response.json();
      if (response.ok) {
        dispatch(reloadCalendars(filters));
        dispatch(ReferencesActions.GetReference('calendars'));
        toast.success(`Календарь изменен`);
        updatedCallback();
      } else {
        throw new Error(
            message.message || `Cannot edit calendar`
        );
      }
    } catch (error) {
      console.error("addCalendar", {error});
      toast.error(`${{ error }.error}` || `Ошибка редактирования календаря`);
    } finally {
      dispatch(setLoading(false));
    }
  };
};
export const reloadCalendars = (
  filter: CalendarFilterUIInterface | null = null
) => {
  return async (dispatch: any) => {
    dispatch(setLoading(true));
    try {
      const response: any = await fetch(
          filter ? prepareFilters({...filter}) : `${API_PREFIX}/${AdminItemType.CALENDARS}`,
          prepareFetchParams()
      );

      if (response.ok) {
        const json = await response.json();
        dispatch(getCalendarsSuccess(json.list, json.totalCount));
      } else {
        throw new Error(
          `Cannot reload ${API_PREFIX}/${AdminItemType.CALENDARS}`
        );
      }
    } catch (error) {
      toast.error(`Не удалось загрузить календари`);
      console.error({ error });
      dispatch(getCalendarsFailure(error));
    } finally {
      dispatch(setLoading(false));
    }
  };
};
