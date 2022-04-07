import {
    DEFAULT_PROJECTS_REFERENCE,
    DEFAULT_OFFICES_REFERENCE,
    DEFAULT_USERS_REFERENCE,
    DEFAULT_CALENDARS_REFERENCE
} from '../../dummy';

type preloadElement = {
    [key: string]: any
}

const preloadList: preloadElement = {
    "projects": {
        reference_key: "projects",
        title: "Projects",
        dummy: DEFAULT_PROJECTS_REFERENCE
    },

    "offices": {
        title: "Offices",
        reference_key: "offices",
        dummy: DEFAULT_OFFICES_REFERENCE
    },

    "users": {
        title: "Employee",
        reference_key: "employee",
        dummy: DEFAULT_USERS_REFERENCE
    },

    "calendars": {
        title: "Calendars",
        reference_key: "calendars",
        dummy: DEFAULT_CALENDARS_REFERENCE
    },

    /*"system": {
        title:"",
        reference_key: "system"
    },*/
};

export default preloadList