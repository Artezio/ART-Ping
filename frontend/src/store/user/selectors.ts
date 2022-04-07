import {NAMESPACE} from "./constants";


export const getCurrentUser = (state: any) => state[NAMESPACE]
export const getCurrentUserRoles = (state: any) => state[NAMESPACE].roles

