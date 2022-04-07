import { API_PREFIX } from '../../constants';
import preloadList from './preload';
import { prepareFetchParams } from '../shared';

import {
    GET_REFERENCES_LIST,
    REFERENCE_RECEIVED,
    REFERENCE_BATH_RECEIVED,
    GET_LIST,
} from './types';

const receiveReference = (list: any, name: string) => {
    return {
        type: REFERENCE_RECEIVED,
        payload: {
            name,
            list
        }
    };
}

const receiveBathReference = (payload: any) => {
    return {
        type: REFERENCE_BATH_RECEIVED,
        payload
    };
}

const requestReference = (name: string, getState: any, dispatch: any) => {
    const ref: any = preloadList[name];
    try {
        fetch(`${API_PREFIX}/references?${new URLSearchParams({ refkey: ref.reference_key })}`, prepareFetchParams())
            .then(response => {
                if (response.status !== 200) {
                    throw new Error(`${API_PREFIX}/references?${new URLSearchParams({ refkey: ref.reference_key })} not loaded`);
                }

                response
                    .json()
                    .then((json: any) => dispatch(receiveReference(json, ref.reference_key)))
                    .catch(error => {
                        console.error({ point: 'requestReference:parceJson', ref, response, error });
                        dispatch(receiveReference(ref.dummy, ref.reference_key));
                    });
            })
            .catch(error => {
                console.error({ point: 'requestReference:fetch', ref, error });
                dispatch(receiveReference(ref.dummy, ref.reference_key));
            });
    } catch (error) {
        console.error({ point: 'requestReference:catch', ref, error });
        dispatch(receiveReference(ref.dummy, ref.reference_key));
    }
}

const requestReferenceList = async (dispatch: any) => {
    let bathLoaded: any = {};
    for (let i in preloadList) {
        const ref: any = preloadList[i];
        try {
            const response = await fetch(`${API_PREFIX}/references?${new URLSearchParams({ refkey: ref.reference_key })}`, prepareFetchParams());
            if (response.status !== 200) {
                throw new Error(`${API_PREFIX}/references?${new URLSearchParams({ refkey: ref.reference_key })} not loaded`);
            }

            response
                .json()
                .then((json: any) => {
                    bathLoaded[ref.reference_key] = {
                        'list': json
                    };
                })
                .catch(error => {
                    console.error({ point: 'requestReference:parceJson', ref, response, error });
                    bathLoaded[ref.reference_key] = {
                        'list': ref.dummy
                    };
                });

        } catch (error) {
            console.error({ point: 'requestReference:catch', ref, error });
            bathLoaded[ref.reference_key] = {
                'list': ref.dummy
            };
        }
    }
    dispatch(receiveBathReference(bathLoaded));
}

export const GetReferencesList = () => {
    return (dispatch: any, getState: any) => {
        requestReferenceList(dispatch);
        return dispatch({
            type: GET_REFERENCES_LIST
        });
    }
}

export const GetReference = (name: string) => {
    return (dispatch: any, getState: any) => {
        requestReference(name, getState, dispatch);
        return dispatch({
            type: GET_LIST
        });
    }
};
