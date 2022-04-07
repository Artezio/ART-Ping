import {API_PREFIX} from '../../constants';
import {
    LIST_RECEIVED,
    GET_LIST,
    GET_APP_INFO, APP_INFO_RECEIVED,
} from './types';
import {
    SET_SYSTEMS_LOADING,
} from "./constants";
import DEFAULT_SETTINGS_LIST from './defaultSettings';
import {prepareFetchParams} from '../shared';
import {toast} from "react-toastify";
import createAction from "../../common/utils/createAction";

const setLoading = (isLoading: boolean) =>
    createAction(SET_SYSTEMS_LOADING, isLoading);

const receiveList = (list: any) => {
    return {
        type: LIST_RECEIVED,
        payload: list
    };
}

const prepareSettingsList = (params: any) => {
    let preparedParams = [...DEFAULT_SETTINGS_LIST];
    if (params) {
        preparedParams.map((param) => {
            if (params[param.name]) {
                param.value = params[param.name]
            }
        })
    }
    return preparedParams;
}


const requestList = (
    filterParams: any,
    getState: any,
    dispatch: any
) => {
    const fetchParams = prepareFetchParams({
        method: 'GET',
        body: filterParams || getState().filtersState
    });
    fetch(API_PREFIX + '/system-settings/list', fetchParams)
        .then((response: any) => {
            if (response.status === 200) {
                response
                    .json()
                    .then((json: any) => {
                        dispatch(receiveList(prepareSettingsList(json)));
                    })
            } else {
                console.error({point: 'requestList:fetch:response', response});
                dispatch(receiveList(DEFAULT_SETTINGS_LIST));
            }
        })
        .catch(error => {
            console.error({point: 'requestList:fetch:error', error});
            dispatch(receiveList(DEFAULT_SETTINGS_LIST));
        });
}

export const getList = (filterParams: any = null) => {
    return (dispatch: any, getState: any) => {
        requestList(filterParams, getState, dispatch);
        return {
            type: GET_LIST
        };
    }
};

export const saveSettings = (settingsParams: any) => {
    const requestParams: any = {
        jwtTokenValidity: 86400,
        ...settingsParams,
    };

    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            const response = await fetch(
                `${API_PREFIX}/system-settings/update`,
                prepareFetchParams({
                    method: "PUT",
                    body: JSON.stringify(requestParams),
                })
            );

            if (response.ok) {
                toast.success(`Настройки успешно сохранены`);
            } else {
                const message = await response.json();
                throw new Error(`${message.message}`);
            }
        } catch (error) {
            console.log({error}.error);
            toast.error(`${{error}.error}`);
        } finally {
            dispatch(setLoading(false));
        }
    };
}

const receiveAppInfo = (payload: any) => {
    return {
        type: APP_INFO_RECEIVED,
        payload: payload || {}
    };
}

export const requestPic = () => {
    const fetchParams = prepareFetchParams();
    return fetch(API_PREFIX + '/gaming/randomfilebycurrentemployee', fetchParams)
        .then((response) => response.blob())
        .then((blob) => {
            const imageUrl = URL.createObjectURL(blob);
            return imageUrl;
        });
}

const requestAppInfo = (dispatch: any) => {
    const fetchParams = prepareFetchParams();
    fetch(API_PREFIX + '/version', fetchParams)
        .then((response: any) => {
            if (response.status === 200) {
                response
                    .json()
                    .then((json: any) => {
                        dispatch(receiveAppInfo(json));
                    })
            } else {
                console.error({point: 'requestAppInfo:fetch:response', response});
                dispatch(receiveAppInfo({}));
            }
        })
        .then(json => {
            dispatch(receiveAppInfo(json));
        })
        .catch(error => {
            console.error({point: 'requestAppInfo:fetch:error', error});
            dispatch(receiveAppInfo({}));
        });
}

export const loadAppInfo = () => {
    return (dispatch: any) => {
        requestAppInfo(dispatch);
        return {
            type: GET_APP_INFO
        };
    }
}

export const importDataAction = async (file: File) => {
    let formData = new FormData();
    formData.append("file", file);
    await fetch(API_PREFIX + '/import',prepareFetchParams(
        {
            method: "POST",
            body: formData,
        }))
        .then((response: any) => {
            if (response.status === 200) {
                response
                    .json()
                    .then((json: any) => {
                        if (json.message) {
                            toast.info(`${json.message}`);
                        }
                    })
            } else {
                console.error({point: 'requestAppInfo:fetch:response', response});
            }
        })
        .catch(error => {
            console.error({point: 'requestAppInfo:fetch:error', error});
        });
}
