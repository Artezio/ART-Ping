import {Action} from "redux";
import {
    FILTER_EMPLOYEE_CHECKS_FAILURE,
    FILTER_EMPLOYEE_CHECKS_SUCCESS,
    FILTER_EMPLOYEE_CHECKS_LOADING, DELETE_CHECK_PERIOD_SUCCESS, DELETE_CHECK_DATE_SUCCESS,
} from "./constants";
import {add} from "date-fns";
import {DELETE_EMPLOYEES_SUCCESS, DELETE_GROUP_EMPLOYEES_SUCCESS} from "../employees/constants";
import {EmployeeInterface} from "../employees";

//
// export interface ToolbarFilterInterface {
//     officeId: string,
// //     projectId: string,
//     searchString: any,
//     period: {
//         type: string,
//         from: string,
//         to: string,
// }
//
// }
export interface State {
    ui: {
        isLoading: boolean;
    };
    data: {
        list: Array<EmployeeCheckInterface>;
        total: number;
    };
}

export interface TestInterface {
    startTime: string;
    status: string;
}

export interface DeleteCheckInterface {
    date: string,
    id: string
};

export interface DeleteCheckPeriodInterface {
    endDate: string,
    ids: Array<string>,
    startDate: string,
}

export interface DateInterface {
    date: string;
    type: string;
    tests: Array<TestInterface>;
}

export interface EmployeeCheckInterface {
    id: string;
    firstName: string;
    lastName: string;
    dates: Array<DateInterface>;
}

export interface EmployeeCheckFilterUIInterface {
    officeId: string | null;
    projectId: string | null;
    searchString: string;
    period: {
        type: string;
        from: Date;
        to: Date;
    };
    pageNumber: number;
    pageSize: number;
    roleName: string | null;
    sort: [];
    onlyActiveEmployees: boolean;
}

export interface EmployeeCheckFilterInterface {
    endDate: string;
    startDate: string;

}

export interface PlanForEmployeesInterface {
    dailyChecks: number;
    endDate: string;
    ids: Array<string>;
    startDate: string;
}

export interface DownloadButtonInterface {
    employeeIds: Array<string>;
    endPeriod: string;
    startPeriod: string;
}

export interface CheckNowInterface extends Array<[]> {
}

export interface GetEmployeesCheckFailureAction extends Action {
    type: typeof FILTER_EMPLOYEE_CHECKS_FAILURE;
    payload: any;
}

export interface GetEmployeesCheckSuccessAction extends Action {
    type: typeof FILTER_EMPLOYEE_CHECKS_SUCCESS;
    payload: Array<EmployeeCheckInterface>;
}

export interface SetEmployeesCheckLoadingAction extends Action {
    type: typeof FILTER_EMPLOYEE_CHECKS_LOADING;
    payload: boolean;
}

export interface DeleteCheckDateAction extends Action {
    type: typeof DELETE_CHECK_DATE_SUCCESS;
    payload?: DeleteCheckInterface;
}

export interface DeleteCheckPeriodAction extends Action {
    type: typeof DELETE_CHECK_PERIOD_SUCCESS;
    payload?: Array<DeleteCheckInterface>;
}

export type ListActions =
    | GetEmployeesCheckFailureAction
    | GetEmployeesCheckSuccessAction
    | SetEmployeesCheckLoadingAction
    | DeleteCheckDateAction
    | DeleteCheckPeriodAction;

// export enum Type {
//     EMPLOYEES = employees_check,
// }
