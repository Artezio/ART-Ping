import { path } from "ramda";

import { NAMESPACE } from "./constants";
import {createSelector} from "reselect";



// export const getUsersType = createSelector(
//     (state, projectId) => projectId,
//     // getAllProjects,
//     (projectId, projects = []) => {
//         const { attributes: { projectTypeInternalName } = {} } =
//         projects.find(({ id }) => "" + projectId === "" + id) || {};
//         return projectTypeInternalName;
//     }
// );
// export const getProjectType = createSelector(
//     (state, projectId) => projectId, getAllProjects, (projectId, projects = []) => {
//         const { attributes: { projectTypeInternalName } = {} } =
//         projects.find(({ id }) => "" + projectId === "" + id) || {};
//         return projectTypeInternalName;
//     }
// );

const dataPath: (...args: any) => any = (...args: any) =>
  path([NAMESPACE, "data", ...args]);
const uiPath: (...args: any) => any = (...args: any) =>
  path([NAMESPACE, "ui", ...args]);

export const getEmployees = dataPath("list");
export const getTotal = dataPath("total");
export const isEmployeesLoading = uiPath("isLoading");





