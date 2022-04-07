import {
    DeleteCheckPeriodInterface,
    EmployeeCheckFilterUIInterface,
    EmployeeCheckInterface,
    PlanForEmployeesInterface,
} from "./types";
import {
    FILTER_EMPLOYEE_CHECKS_FAILURE,
    FILTER_EMPLOYEE_CHECKS_LOADING,
    FILTER_EMPLOYEE_CHECKS_SUCCESS,
} from "./constants";
import {toast} from "react-toastify";
import createAction from "../../common/utils/createAction";
import {prepareFetchParams} from "../../store";
import {API_PREFIX} from "../../constants";
import {endOfWeek, format} from "date-fns";
import {weekStartsOn} from "./utils";


interface EmployeeCheckProcessedFilterUIInterface {
    officeId: string | null;
    projectId: string | null;
    searchString: string;
    startPeriod: string;
    endPeriod: string;
    pageNumber: number;
    pageSize: number;
    roleName: string | null;
    sort: [];
    onlyActiveEmployees: boolean,
}

function filterEmployeeChecksSuccess(
    list: Array<EmployeeCheckInterface>,
    total: number
) {
    return createAction(FILTER_EMPLOYEE_CHECKS_SUCCESS, {list, total});
}

const prepareFilters = (filters: EmployeeCheckProcessedFilterUIInterface) => {

    const generateSortName = (fieldName: string) => {
        if (fieldName === 'name') {
            return 'lastName';
        }
        if (fieldName === 'Active') {
            return 'user.active';
        }
        return fieldName;
    }

    const officeIdString = filters?.officeId ? `&officeId=${filters?.officeId}` : ``;
    const projectIdString = filters?.projectId ? `&projectId=${filters?.projectId}` : ``;
    const onlyActiveEmployees = `&onlyActiveEmployees=${filters?.onlyActiveEmployees}`;
    let sortParams = '';

    if (filters && filters?.sort.length > 0) {
        filters?.sort?.forEach((item, index) => {
            sortParams += `&sorting[${index}].asc=${item['sort'] === 'asc' ? 'true' : 'false'}&sorting[0].field=${generateSortName(item['field'])}`
        })
    }
    const fetchString = `${API_PREFIX}/employee-checks${
        filters
            ? `?pageNumber=${filters?.pageNumber ?? 0}` +
            `&pageSize=${filters?.pageSize ?? 20}` +
            `&startPeriod=${filters?.startPeriod}` +
            `&endPeriod=${filters?.endPeriod}` +
            encodeURI(officeIdString) +
            encodeURI(projectIdString) +
            encodeURI(onlyActiveEmployees) +
            encodeURI(sortParams) +
            `&searchString=${filters?.searchString}`
            : ""
    }`
    return fetchString;
}

function addPlanForEmployeesSuccess(list: Array<PlanForEmployeesInterface>) {
    return createAction(FILTER_EMPLOYEE_CHECKS_SUCCESS, list);
}

const getPlanForEmployeesFailure = (error: any) =>
    createAction(FILTER_EMPLOYEE_CHECKS_FAILURE, error);

const setLoading = (isLoading: boolean) =>
    createAction(FILTER_EMPLOYEE_CHECKS_LOADING, isLoading);
let currentDate = new Date();
const defaultFilterState: EmployeeCheckFilterUIInterface = {
    officeId: null,
    projectId: null,
    searchString: "",
    period: {
        type: 'week',
        from: currentDate,
        to: endOfWeek(currentDate, {weekStartsOn})
    },
    roleName: null,
    pageNumber: 0,
    pageSize: 20,
    sort: [],
    onlyActiveEmployees: true,
};


const processFilter = ({
                           officeId,
                           projectId,
                           searchString,
                           pageSize,
                           pageNumber,
                           roleName,
                           period: {from, to},
                           sort,
                           onlyActiveEmployees,
                       }: EmployeeCheckFilterUIInterface) => {
    let newVar = {
        endPeriod: format(to, "yyyy-MM-dd"),
        startPeriod: format(from, "yyyy-MM-dd"),
        officeId,
        projectId,
        searchString,
        pageSize,
        pageNumber,
        roleName,
        sort,
        onlyActiveEmployees,
    };
    return newVar;
};

export const filterEmployeeChecks = (
    filter: EmployeeCheckFilterUIInterface
) => {
    const processedFilter = processFilter(filter);

    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            const response: any = await fetch(
                filter ? prepareFilters({...processedFilter}) : `${API_PREFIX}/employee-checks`,
                prepareFetchParams()
            );
            if (response.ok) {
                const json = await response.json();
                dispatch(filterEmployeeChecksSuccess(json.list, json.totalCount));
            } else {
                throw new Error(
                    `Cannot reload ${response.status}: ${response.statusText}`
                );
            }
        } catch (error) {
            console.log({error});
            toast.error(`Ошибка загрузки списка проверок сотрудников`);
        } finally {
            dispatch(setLoading(false));
        }
    };
};

export const insertPlanForEmployees = (
    newPlan: PlanForEmployeesInterface,
    filter: EmployeeCheckFilterUIInterface
) => {
    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            const response = await fetch(
                `${API_PREFIX}/employee-checks/planForEmployees`,
                prepareFetchParams({
                    method: "POST",
                    body: JSON.stringify(newPlan),
                })
            );
            if (response.ok) {
                dispatch(filterEmployeeChecks(filter));
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
};

export const checkNowForEmployees = (
    checkedEmployees: Array<string>,
    filter: EmployeeCheckFilterUIInterface
) => {
    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            const response = await fetch(
                `${API_PREFIX}/employee-checks/checkNow`,
                prepareFetchParams({
                    method: "POST",
                    body: JSON.stringify(checkedEmployees),
                })
            );
            if (response.ok ) {
                dispatch(filterEmployeeChecks(filter));
            } else {
                throw new Error();
            }
        } catch (error) {
            console.log({error});
            toast.error(`Ошибка создания проверки`);
        } finally {
            dispatch(setLoading(false));
        }
    };
};

export const cancelChecksPeriodAction = (
    selectedEmployee: Array<string>, filter: EmployeeCheckFilterUIInterface) => {

    const params: DeleteCheckPeriodInterface = {
        startDate: format(filter.period.from, "yyyy-MM-dd"),
        endDate: format(filter.period.to, "yyyy-MM-dd"),
        ids: [...selectedEmployee]

    }
    return async (dispatch: any) => {
        dispatch(setLoading(true));
        try {
            const response = await fetch(
                `${API_PREFIX}/employee-checks/cancelEmployeesChecks`,

                prepareFetchParams({
                    method: "POST",
                    body: JSON.stringify(params),
                })
            );

            if (response.ok) {
                dispatch(filterEmployeeChecks(filter));
                toast.success(`Проверка удалена`);

            } else {
                throw new Error(`Ошибка удаления ${selectedEmployee}`);
            }
        } catch (error) {
            console.log({error});
            toast.error(`Проверка не удалена`);
        } finally {
            dispatch(setLoading(false));
        }
    };
};
// export const reloadChecks = (
//     filter: EmployeeCheckFilterUIInterface | null = null
// ) => {
//     const officeIdString = encodeURI(filter?.officeId ? `&officeId=${filter?.officeId}` : ``);
//     const projectIdString = encodeURI(filter?.projectId ? `&projectId=${filter?.projectId}` : ``);
//     const roleNameString = encodeURI(filter?.roleName ? `&roleName=${filter?.roleName}` : ``);
//     return async (dispatch: any, getState: any) => {
//         dispatch(setLoading(true));
//         try {
//             const response = await fetch(
//                 `${API_PREFIX}/${AdminItemType.CHECKS}${
//                     filter
//                         ? `?pageNumber=${filter?.pageNumber ?? 0}` +
//                         `&pageSize=${filter?.pageSize ?? 20}` +
//                         officeIdString +
//                         projectIdString +
//                         roleNameString +
//                         `&searchString=${filter?.searchString}`
//                         : ""
//                 }`,
//                 prepareFetchParams()
//             );
//
//             if (response.ok) {
//                 const json = await response.json();
//                 dispatch(addPlanForEmployeesSuccess(json.list));
//                 // dispatch(getOfficesSuccess(DEFAULT_OFFICES_LIST));
//             } else {
//                 throw new Error(`Cannot reload `);
//             }
//         } catch (error) {
//             toast.error(`Cannot reload`);
//             console.error({error});
//             dispatch(getPlanForEmployeesFailure(error));
//         } finally {
//             dispatch(setLoading(false));
//         }
//     };
