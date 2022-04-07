import {OfficeFilterUIInterface, OfficeInterface} from "./types";
import {GET_OFFICES_FAILURE, GET_OFFICES_SUCCESS, SET_OFFICES_LOADING,} from "./constants";
import {toast} from "react-toastify";
import createAction from "../../common/utils/createAction";
import {AdminItemType} from "../../common/types/AdminItemType";
import {prepareFetchParams} from "../../store";
import {API_PREFIX} from "../../constants";
import * as ReferencesActions from "../../store/references/actions";

function getOfficesSuccess(list: Array<OfficeInterface>, total: number) {
    return createAction(GET_OFFICES_SUCCESS, {list, total});
}

const prepareFilters = (filters: OfficeFilterUIInterface) => {
    const generateSortName = (fieldName: string) => {
        if (fieldName === 'calendarName') {
            return 'calendar.name';
        }
        return fieldName;
    }

    let sortParams = '';

    if (filters && filters?.sort.length > 0) {
        filters?.sort?.forEach((item, index)=> {
            sortParams += `&sorting[${index}].asc=${item['sort'] === 'asc' ? 'true' : 'false' }&sorting[0].field=${generateSortName(item['field'])}`
        })
    }
    const fetchString = `${API_PREFIX}/${AdminItemType.OFFICES}${
        filters
            ? `?pageNumber=${filters?.pageNumber ?? 0}` +
            `&pageSize=${filters?.pageSize ?? 20}` +
            encodeURI(sortParams)+
            `&searchString=${filters?.searchString}`
            : ""
    }`
    return fetchString;
}

const getOfficesFailure = (error: any) =>
    createAction(GET_OFFICES_FAILURE, error);

const setLoading = (isLoading: boolean) =>
    createAction(SET_OFFICES_LOADING, isLoading);

export const addOffice = (newOffice: OfficeInterface, filtersState: OfficeFilterUIInterface) => {

    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {

            const response = await fetch(
                `${API_PREFIX}/${AdminItemType.OFFICES}`,
                prepareFetchParams({
                    method: "POST",
                    body: JSON.stringify(newOffice),
                })
            );
            if (response.ok) {
                dispatch(reloadOffices(filtersState));
                dispatch(ReferencesActions.GetReference('offices'));
                toast.success(`Новый офис создан`);
            } else {
                const message = await response.json();
                throw new Error(
                    message.message || `Ошибка создания офиса`
                );
            }
        } catch (error) {
            dispatch(reloadOffices());
            toast.error(`${{error}.error}` || `Ошибка создания офиса`);
        } finally {
            dispatch(setLoading(false));
        }
    };
};
export const deleteGroupOfficesAction = (
    deletedOffice: Array<OfficeInterface>
) => {
    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            throw new Error(`Not implemented yet`);
        } catch (error) {
            console.log({error});
            toast.error(`Not implemented yet`);
        } finally {
            dispatch(setLoading(false));
        }
    };
};
export const deleteOfficeAction = (deletedOffice: OfficeInterface, filters: OfficeFilterUIInterface) => {
    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            const response = await fetch(
                `${API_PREFIX}/${AdminItemType.OFFICES}/${deletedOffice.id}`,
                prepareFetchParams({
                    method: "DELETE",
                })
            );

            if (response.ok) {
                dispatch(reloadOffices(filters));
                dispatch(ReferencesActions.GetReference('offices'));
                toast.success(`Офис удален`);
            } else {
                const message = await response.json();
                throw new Error(message.message || `Ошибка удаления офиса ${deletedOffice}`);
            }
        } catch (error) {
            console.log({error});
            toast.error(`${{error}.error}` || `Офис не удален`);
        } finally {
            dispatch(setLoading(false));
        }
    };
};

export const editOfficeAction = (editedOffice: OfficeInterface, updatedCallback: any, filtersState: OfficeFilterUIInterface) => {
    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            const response = await fetch(
                `${API_PREFIX}/${AdminItemType.OFFICES}`,
                prepareFetchParams({
                    method: "PUT",
                    body: JSON.stringify(editedOffice),
                })
            );
            let message = await response.json();
            if (response.ok) {
                dispatch(reloadOffices(filtersState));
                dispatch(ReferencesActions.GetReference('offices'));
                toast.success(`Офис отредактирован`);
                updatedCallback();
            } else {
                throw new Error(
                    message.message || `Ошибка редактирования офиса`
                );
            }
        } catch (error) {
            console.log({error});
            toast.error(`${{error}.error}` || `Ошибка редактирования офиса`);
        } finally {
            dispatch(setLoading(false));
        }
    };
};
export const reloadOffices = (
    filter: OfficeFilterUIInterface | null = null
) => {
    return async (dispatch: any, getState: any) => {
        dispatch(setLoading(true));
        try {
            const response: any = await fetch(
                filter ? prepareFilters({...filter}) : `${API_PREFIX}/${AdminItemType.OFFICES}`,
                prepareFetchParams()
            );
            if (response.ok) {
                const json = await response.json();
                dispatch(getOfficesSuccess(json.list, json.totalCount));
                // dispatch(getOfficesSuccess(DEFAULT_OFFICES_LIST));
            } else {
                throw new Error(`Ошибка загрузки `);
            }
        } catch (error) {
            toast.error(`Ошибка загрузки списка офисов`);
            console.error({error});
            dispatch(getOfficesFailure(error));
        } finally {
            dispatch(setLoading(false));
        }
    };
};
