import createAction from "../../../src/common/utils/createAction";
import { toast } from "react-toastify";
import { CalendarInterface, OfficeInterface, ProjectInterface, Type, UserInterface } from "./types";
import { GET_ITEMS_FAILURE, GET_ITEMS_SUCCESS, SET_ITEMS_LOADING } from "./constants";
import {API_PREFIX} from '../../constants';


export const getItems = (itemsType: Type) => {


    return async (dispatch: any, getState: any) => {
        dispatch(setLoading(itemsType, true));
        try {
            const response = await fetch(`${API_PREFIX}/${itemsType}`, {
                method: "GET",
                mode: 'cors'
            });

            if (response.ok) {
                const json = await response.json();
                dispatch(getItemsSuccess(itemsType, [...json]));
            } else {
                throw new Error((`Cannot reload ${itemsType}`))
            }
        } catch (error) {
            toast.error(`Cannot reload ${itemsType}`);
            console.error({ error });
            dispatch(getItemsFailure(itemsType, error));

        } finally {
            dispatch(setLoading(itemsType, false));
        }
    }
};

function getItemsSuccess(type: Type, list: Array<UserInterface | ProjectInterface | OfficeInterface | CalendarInterface>) {
    return createAction(GET_ITEMS_SUCCESS, {
        type,
        list
    }
    )
}

const getItemsFailure = (type: Type, error: any) =>
    createAction(GET_ITEMS_FAILURE, { type: type, error });

export const addItem = (newItem: UserInterface | ProjectInterface | OfficeInterface | CalendarInterface, type: Type) => {


    return async (dispatch: any) => {
        dispatch(setLoading(type, true));
        try {
            const response = await fetch(`${API_PREFIX}/${type}`, {
                method: "POST",
                mode: 'cors',
                headers: {
                    "Content-Type": "application/json; charset=utf-8"
                },
                body: JSON.stringify(newItem)
            });

            if (response.ok) {
                dispatch(getItems(type));
                toast.success((`Item successfully created.`));
            } else {
                throw new Error(`${response.status}: ${response.statusText}`)
            }
        } catch (error) {
            console.log({ error })
            toast.error(`Item not created.`);
        } finally {
            dispatch(setLoading(type, false));
        }
    }
};
const setLoading = (type: Type, isLoading: boolean) =>
    createAction(SET_ITEMS_LOADING, { type, isLoading });

export const deletedItemAction = (deletedItem: UserInterface | ProjectInterface | OfficeInterface | CalendarInterface, type: Type) => {
    return async (dispatch: any) => {
        dispatch(setLoading(type, true));
        try {

            const response = await fetch(`${API_PREFIX}/${type}/${deletedItem.id}`, {
                method: "DELETE"
            });

            if (response.ok) {
                dispatch(getItems(type));
                toast.success(`Item successfully deleted.`);
            } else {
                throw new Error(`Cannot delete ${deletedItem}`)
            }

        } catch (error) {
            console.log({ error })
            toast.error(`Item not deleted.`);
        } finally {
            dispatch(setLoading(type, false));
        }
    }
};
export const deleteGroupAction = (deletedItems: Array<UserInterface | ProjectInterface | OfficeInterface | CalendarInterface>, type: Type) => {
    return async (dispatch: any) => {
        dispatch(setLoading(type, true));
        try {
            throw new Error(`Not implemented yet`)
        } catch (error) {
            console.log({ error })
            toast.error(`Not implemented yet`);
        } finally {
            dispatch(setLoading(type, false));
        }
    }
};
export const editItemAction = (editedItem: UserInterface | ProjectInterface | OfficeInterface | CalendarInterface, type: Type) => {
    return async (dispatch: any) => {
        dispatch(setLoading(type, true));
        try {
            const response = await fetch(`${API_PREFIX}/${type}`, {
                method: "PUT",
                mode: 'cors',

                headers: {
                    "Content-Type": "application/json; charset=utf-8"
                },
                body: JSON.stringify(editedItem)
            });
            if (response.ok) {
                dispatch(getItems(type));
                toast.success(`Item successfully edited.`);
            } else {
                throw new Error(`Cannot edit ${editedItem}`)
            }
        } catch (error) {
            console.log({ error })
            toast.error(`Item not saved.`);
        } finally {
            dispatch(setLoading(type, false));
        }
    }
};