import * as React from "react";
import GroupIcon from '@material-ui/icons/Group';
import BusinessIcon from '@material-ui/icons/Business';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import SettingsIcon from '@material-ui/icons/Settings';
import DateRangeIcon from '@material-ui/icons/DateRange';
import DashboardIcon from '@material-ui/icons/Dashboard';
import Dashboard from './components/Dashboard';
import {PageOffices} from '../../../modules/offices';
import {PageProjects} from '../../../modules/projects';
import {PageUsers as PageEmployees} from '../../../modules/employees';
import PageSystem from '../../../modules/systems/Container';
import {PageCalendar} from "../../../modules/calendars";
import DataImport from "../../../modules/dataImport/Container";

export function getAdminItemSeeds(hasAccessToPageSystemSettings: any,
                                  hasAccessToPageProjects: any,
                                  hasAccessToPageCalendars: any,
                                  hasAccessToPageOffices: any,
                                  hasAccessToPageEmployees: any,
                                  hasAccessToPageDataImport: any,
                                  iconStyle: any,
) {
    return [
        {
            title: "Dashboard",
            path: "/admin",
            exact: true,
            disabled:  true,
            description: "Main panel of administrative section",
            component: <Dashboard/>,
            icon: <DashboardIcon style={iconStyle}/>
        },
        {
            title: "Calendars",
            path: "/admin/calendar",
            description: "Configure your calendars first",
            hasAccess: hasAccessToPageCalendars,
            component: <PageCalendar/>,
            icon: <DateRangeIcon style={iconStyle}/>
        },
        {
            title: "Offices",
            path: "/admin/offices",
            description: "Group employees by offices",
            hasAccess: hasAccessToPageOffices,
            component: <PageOffices/>,
            icon: <BusinessIcon style={iconStyle}/>
        },
        {
            title: "Employees",
            path: "/admin/users",
            description: "Add employees to the ART-PING system",
            hasAccess: hasAccessToPageEmployees,
            component: <PageEmployees/>,
            icon: <GroupIcon style={iconStyle}/>
        },
        {
            title: "Projects",
            path: "/admin/projects",
            description: "Group employees by projects",
            hasAccess: hasAccessToPageProjects,
            component: <PageProjects/>,
            icon: <AccountTreeIcon style={iconStyle}/>
        },
        {
            title: "System settings",
            path: "/admin/system-settings",
            hasAccess: hasAccessToPageSystemSettings,
            description: "System-wide settings",
            component: <PageSystem/>,
            icon: <SettingsIcon style={iconStyle}/>
        },
        {
            title: "Data import",
            path: "/admin/data-import",
            hasAccess: hasAccessToPageDataImport,
            description: "DB import to the server",
            component: <DataImport/>,
            icon: <SettingsIcon style={iconStyle}/>
        },
    ];
};
