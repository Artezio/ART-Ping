import {path} from "ramda";

import {NAMESPACE} from "./constants";

const dataPath: (...args: any) => any = (...args: any) => path([NAMESPACE, "data", ...args]);

export const getUsers = dataPath("users", "list");
export const isUsersLoading: (...args: any) => boolean = dataPath("users", "isLoading");

export const getOffices = dataPath('offices', 'list');
export const isOfficesLoading = dataPath("offices", "isLoading");

export const getProjects = dataPath("projects", "list");
export const isProjectsLoading = dataPath("projects", "isLoading");

export const getSystems = dataPath("system", "list");
export const isSystemLoadings = dataPath("system", "isLoading");

export const isCalendarLoading = dataPath("calendars", "isLoading");
export const getCalendars = dataPath("calendars","list");
