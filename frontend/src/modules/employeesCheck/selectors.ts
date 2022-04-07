import {intersection, path} from "ramda";

import {NAMESPACE} from "./constants";
import {createSelector} from "reselect";
import {getCurrentUserRoles} from "../../store/user/selectors";
import {ADMIN, DIRECTOR, HR, PM, USER} from "../../store/user/constants";

const dataPath: (...args: any) => any = (...args: any) =>
    path([NAMESPACE, "data", ...args]);
const uiPath: (...args: any) => any = (...args: any) =>
    path([NAMESPACE, "ui", ...args]);

export const getEmployeeChecks = dataPath("list");
export const getTotal = dataPath("total");
export const isEmployeeChecksLoading = uiPath("isLoading");


export const hasAccessEmployeesMyChecksSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [PM, DIRECTOR, HR, USER]).length > 0);
export const hasAccessEmployeesCheckSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN, PM, DIRECTOR, HR]).length > 0);
export const hasAccessToPageSystemSettingsSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN]).length > 0);
export const hasAccessToPageProjectsSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN, PM]).length > 0);
export const hasAccessToPageCalendarsSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN, HR]).length > 0);
export const hasAccessToPageOfficesSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN, HR, DIRECTOR]).length > 0);
export const hasAccessToPageEmployeesSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN, HR]).length > 0);
export const hasAccessToPageDataImportSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN, HR]).length > 0);
export const hasAccessCheckNowSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN,DIRECTOR,HR,PM]).length > 0);
export const hasAccessSetAutoCheckSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN,DIRECTOR,HR,PM]).length > 0);
export const hasAccessCancelChecksSelector = createSelector(getCurrentUserRoles,
    roles => intersection(roles, [ADMIN,DIRECTOR,HR,PM]).length > 0);
export const hasAccessAddEditEmployeeSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN,HR]).length > 0;
    })
export const hasAccessEditAdminRoleSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN]).length > 0;
    })
export const hasAccessOfficesSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN, DIRECTOR, HR]).length > 0;
    });
export const hasAccessAddOfficesSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN]).length > 0;
    });
export const hasAccessEditOfficesSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN]).length > 0;
    });
export const hasAccessProjectsSelector = createSelector(getCurrentUserRoles,
    roles => {
    return intersection(roles, [ADMIN, DIRECTOR, PM]).length > 0;
    });
export const hasAccessAddProjectsSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN,DIRECTOR]).length > 0;
    });
export const hasAccessEditProjectsSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN]).length > 0;
    });
export const hasAccessAddCalendarsSelector = createSelector(getCurrentUserRoles,
    roles => {
    return intersection(roles, [ADMIN]).length > 0
    });
export const hasAccessEditCalendarsSelector = createSelector(getCurrentUserRoles,
    roles => {
        return intersection(roles, [ADMIN,HR]).length > 0
    });
export const hasAccessAddEditVacationsSelector = createSelector(getCurrentUserRoles,
    roles => {
    return intersection(roles, [ADMIN,HR]).length > 0
    });
export const hasAccessAddEditGraphicsSelector = createSelector(getCurrentUserRoles,
    roles => {
    return intersection(roles, [ADMIN,HR,PM,DIRECTOR,USER]).length > 0
    });
export const hasAccessViewAndEditEmployees = createSelector(getCurrentUserRoles,
        active =>{
    return intersection(active,[true]).length>0
        });


// export const isListEmployeesCheckDisabledDirector = createSelector(getCurrentUserRoles, roles => {
//         if (roles.includes("DIRECTOR")) {
//             return false;
//         }
//         return true;
//     }
// )
// export const isListEmployeesCheckDisabledPM = createSelector(getCurrentUserRoles, roles => {
//         if (roles.includes("PM")) {
//             return false;
//         }
//         return true;
//     }
// )
