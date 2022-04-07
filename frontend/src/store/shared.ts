import { toast } from "react-toastify";
import { API_PREFIX } from '../constants';
export const prepareFetchParams = (params: any = {}) => {
    const preset: any = {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }
    }
    if (params?.body && params.body instanceof FormData) {
        delete preset.headers['Content-Type'];
    }
    const jwt = localStorage.getItem('jwt');
    if (jwt) {
        preset.headers['Authorization'] = `Bearer ${jwt}`;
    }
    const prepared: RequestInit = Object.assign({}, preset, params);
    return prepared;
};

export const getEntityById = async (entityId: string, entityType: string,callback: any = () => { }) => {
    const response: any = await fetch(`${API_PREFIX}/${entityType}/${entityId}`, prepareFetchParams());
    if (response.ok) {
        const json = await response.json();
        callback(json);
    } else {
        toast.error(`Cannot load ${entityType} id:${entityId}`);
        console.error({ response });
    }
};
