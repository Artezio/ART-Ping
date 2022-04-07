import { combineReducers, Reducer } from 'redux';
import { DefaultRootState, TypedUseSelectorHook, useSelector } from 'react-redux';
import { reducer as employee_tests } from './employeeTests/reducer';
import { reducer as references } from './references/reducer';
import { reducer as admin } from './admin/reducer';
import { reducer as user } from './user/reducer';
import { reducer as notifications } from './notifications/reducer';
import { reducer as system } from './system/reducer';
import { ListInterface as SystemState } from './system/types';
import { ProjectsReducer as projects, State as ProjectState } from '../modules/projects';
import { State as EmployeesState, UsersReducer as employees } from '../modules/employees';
import { OfficesReducer as offices, State as OfficeState } from '../modules/offices';
import { CalendarsReducer as calendars, State as CalendarState } from '../modules/calendars';
import { EmployeeChecksReducer as employeeChecks, State as EmployeeChecksState } from '../modules/employeesCheck'

export interface AppRootState extends DefaultRootState {
    //app: any;
    user: any;
    employeeChecks: EmployeeChecksState;
    employee_tests: any;
    references: any;
    admin: any;
    projects: ProjectState;
    employees: EmployeesState;
    offices: OfficeState;
    calendars: CalendarState;
    notifications: any;
    system: SystemState;
}

export const TypedSelector: TypedUseSelectorHook<AppRootState> = useSelector;

export const reducer: Reducer<AppRootState> = combineReducers<AppRootState>({
    // app,
    user,
    employees,
    employeeChecks,
    employee_tests,
    references,
    admin,
    projects,
    offices,
    calendars,
    notifications,
    system
});

export * from './shared';