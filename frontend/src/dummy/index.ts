import default_user from './DefaultUser';
import tests_list from './TestsList';
import projects_list from './ProjectsList';
import offices_list from './OfficesList';
import users_list from './UsersList';
import system_settings from './SystemSettings';
import calendars_list from './CalendarsList';

const uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
  

export const DEFAULT_USER = default_user;
export const DEFAULT_TESTS_LIST = tests_list;
export const DEFAULT_PROJECTS_LIST = projects_list;
export const DEFAULT_OFFICES_LIST = offices_list;
export const DEFAULT_CALENDARS_LIST = calendars_list;

export const DEFAULT_USERS_LIST = users_list;
export const DEFAULT_SYSTEM_SETTINGS = system_settings;

export const DEFAULT_PROJECTS_REFERENCE = [
    {
        id: uuidv4(),
        name: "Project 1",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    },
    {
        id: uuidv4(),
        name: "Project 2",
        description: "Donec quis mauris sed lacus tempor finibus."
    },
    {
        id: uuidv4(),
        name: "Project 3",
        description: "Nam vel lacinia dui."
    }
];
export const DEFAULT_OFFICES_REFERENCE = [
    {
        id: uuidv4(),
        name: "Office 1",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    },
    {
        id: uuidv4(),
        name: "Office 2",
        description: "Donec quis mauris sed lacus tempor finibus."
    },
    {
        id: uuidv4(),
        name: "Office 3",
        description: "Nam vel lacinia dui."
    },
    {
        id: uuidv4(),
        name: "Office 4",
        description: "Nam vel lacinia dui."
    },
    {
        id: uuidv4(),
        name: "Office 5",
        description: "Nam vel lacinia dui."
    }
];
export const DEFAULT_USERS_REFERENCE = [
    {
        id: uuidv4(),
        name: "User 0001",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    },
    {
        id: uuidv4(),
        name: "User 0002",
        description: "Donec quis mauris sed lacus tempor finibus."
    },
    {
        id: uuidv4(),
        name: "User 0003",
        description: "Nam vel lacinia dui."
    },
    {
        id: uuidv4(),
        name: "User 0004",
        description: "Nam vel lacinia dui."
    },
    {
        id: uuidv4(),
        name: "User 0005",
        description: "Nam vel lacinia dui."
    },
    {
        id: uuidv4(),
        name: "User 0006",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    },
    {
        id: uuidv4(),
        name: "User 0007",
        description: "Donec quis mauris sed lacus tempor finibus."
    },
];
export const DEFAULT_CALENDARS_REFERENCE = [
    {
        id: uuidv4(),
        name: "Calendar 22_001",
        description: "Nam vel lacinia dui."
    },
    {
        id: uuidv4(),
        name: "Calendar 22_002",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    },
    {
        id: uuidv4(),
        name: "Calendar 22_003",
        description: "Donec quis mauris sed lacus tempor finibus."
    },
    {
        id: uuidv4(),
        name: "Calendar 22_004",
        description: "Nam vel lacinia dui."
    },
    {
        id: uuidv4(),
        name: "Calendar 22_005",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    },
    {
        id: uuidv4(),
        name: "Calendar 22_006",
        description: "Donec quis mauris sed lacus tempor finibus."
    }
];

export const DEFAULT_SETTINGS_LIST = [
    {
        id: 0,
        name: "pageSize",
        type: 'number',
        title: "Grid page size",
        value: 20
    },
    {
        id: 1,
        name: "checksLimitPerDay",
        type: 'number',
        title: "Limit per day for autochecks",
        value: 16
    },
    {
        id:2,
        name:"dayTypesList",
        type:'array',
        title:"Avaliable day types",
        value:[
            {
                id:0,
                name:"work",
                value:"Рабочий"
            },
            {
                id:2,
                name:"holiday",
                value:"Выходной"
            },
            {
                id:3,
                name:"seek",
                value:"Больничный"
            },
            {
                id:3,
                name:"vacation",
                value:"Отпуск"
            }
        ]
    }
];