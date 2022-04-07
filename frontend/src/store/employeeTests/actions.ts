import { API_PREFIX } from '../../constants';
import { DEFAULT_TESTS_LIST } from '../../dummy';
import { TypedSelector } from '../index';
import {
    LIST_RECEIVED,
    GET_LIST,
} from './types';


const normalizeEmployeeTestsList = (json: any) => {
    const t = (str: string) => {
        return str;
    }
    const list: any = [];

    for (let i in json.list) {
        let row = json.list[i];
        const days: any = {};

        for (let v in row.vacationDates || []) {
            row["days." + row.vacationDates[v]] = t("Vacation");
        }
        delete row.vacationDates;

        for (let s in row.sickDates || []) {
            days["days." + row.sickDates[s]] = t("Sick");
        }
        delete row.sickDates;

        for (let l in row.leaveDate || []) {
            days["days." + row.leaveDate[l]] = t("Leave");
        }
        delete row.leaveDate;

        list.push(row);
    }
    return {
        list
    };
}

const receiveEmployeeTestsList = (list: any) => {
    return {
        type: LIST_RECEIVED,
        payload: normalizeEmployeeTestsList(list)
    };
}

const requestEmployeeTestsList = (
    filterParams: any,
    getState: any,
    dispatch: any
) => {
    try {
        fetch(API_PREFIX + '/ping/staff/tests', {
            method: 'POST',
            mode: 'cors',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8'
            },
            //body: formData,
            body: filterParams || getState().filtersState,
        })
            .then(async response => {
                if (response.status != 200) {
                    throw new Error(response.statusText)
                }
                const json = await response.json();
                return { ...json, status: response.status };
            })
            .then(json => {
                dispatch(receiveEmployeeTestsList(json));
            })
            .catch(error => {
                console.error({ point: 'requestEmployeeList:fetch:error', error });
                dispatch(receiveEmployeeTestsList(DEFAULT_TESTS_LIST));
            });
    } catch (error) {
        console.error({ point: 'requestEmployeeList:catch', error });
        dispatch(receiveEmployeeTestsList(DEFAULT_TESTS_LIST));
    }
}

export const getEmployeeTestsList = (filterParams: any = null) => {
    return (dispatch: any, getState: any) => {
        requestEmployeeTestsList(filterParams, getState, dispatch);
        return {
            type: GET_LIST
        };
    }
};