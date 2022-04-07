import {values} from "ramda";
import {utc} from "moment/moment";
import {tz} from "moment-timezone";

export const NAMESPACE = "offices";

export const SET_OFFICES_LOADING = 'SET_OFFICES_LOADING';
export const GET_OFFICES_SUCCESS = 'GET_OFFICES_SUCCESS';
export const GET_OFFICES_FAILURE = 'GET_OFFICES_FAILURE';
export const DELETE_OFFICES_SUCCESS = 'DELETE_OFFICES_SUCCESS';
export const DELETE_GROUP_OFFICES_SUCCESS = 'DELETE_GROUP_OFFICES_SUCCESS'

// export const UTC-12 (BakerIsland) = "UTC-12";
// export const UTC-11(AmericanSamoa) = "UTC-11";
// export const UTC-10(USA/Hawaii) = "UTC-10";
// export const UTC-9 (USA/Alaska)= "UTC-9";
// export const UTC-8 (Canada/USA/Mexico)= "UTC-8";
// export const UTC-7 (Canada/USA/Mexico)= "UTC-7";
// export const UTC-6(Canada/USA/Mexico/Costa-Rica) = "UTC-6";
// export const UTC-5 (Canada/USA/Mexico/Cuba/Brazil)= "UTC-5";
// export const UTC-4 (Canada/Chili/Brazil)= "UTC-4";
// export const UTC-3(Argentina/Brazil) = "UTC-3";
// export const UTC-2 (Brazil)= "UTC-2";
// export const UTC-1(Portugal) = "UTC-1";
// export const UTC+0(Island/Great Britain/Spain/Ghana) = "UTC0";
// export const UTC+1(Europe/France/Spain/Poland) = "UTC+1";
// export const UTC+2 (Finland/Russia/Israel)= "UTC+2";
// export const UTC+3 (Russia/Belarus/Iraq)= "UTC+3";
// export const UTC+4 (UAE/Armenia/Georgia)= "UTC+4";
// export const UTC+5 (Kazakhstan/Tajikistan/Pakistan)= "UTC+5";
// export const UTC+6 (Kyrgyzstan/Bangladesh) = "UTC+6";
// export const UTC+7(Mongolia/Thailand/Vietnam) = "UTC+7";
// export const UTC+8 (Malaysia/Indonesia/Australia)= "UTC+8";
// export const UTC+9 (Japan/Indonesia)= "UTC+9";
// export const UTC+10 (Papua New Guinea/Australia) = "UTC+10";
// export const UTC+11 (Russia/Solomon Islands/New Caledonia)= "UTC+11";
// export const UTC+12 (Kiribati/Fiji/New Zealand) = "UTC+12";
// const arrayTimezones = ["UTC-12",
//     "UTC-11",
//     "UTC-10",
//     "UTC-9",
//     "UTC-8",
//     "UTC-7",
//     "UTC-6",
//     "UTC-5",
//     "UTC-4",
//     "UTC-3",
//     "UTC-2",
//     "UTC-1",
//     "UTC+0",
//     "UTC+12",
//     "UTC+11",
//     "UTC+10",
//     "UTC+9",
//     "UTC+8",
//     "UTC+7",
//     "UTC+6",
//     "UTC+5",
//     "UTC+4",
//     "UTC+3",
//     "UTC+2",
//     "UTC+1"
// ];
export const Timezones = [
    {value:'UTC-12',name : 'BakerIsland(UTC-12)'},
    {value:'UTC-11',name : 'AmericanSamoa(UTC-11)'},
    {value:'UTC-10',name : 'USA/Hawaii(UTC-10)'},
    {value:'UTC-9',name : 'USA/Alaska(UTC-9)'},
    {value:'UTC-8',name : 'Canada/USA/Mexico(UTC-8)'},
    {value:'UTC-7',name : 'Canada/USA/Mexico(UTC-7)'},
    {value:'UTC-6',name : 'Canada/USA/Mexico/Costa-Rica(UTC-6)'},
    {value:'UTC-5',name : 'Canada/USA/Mexico/Cuba/Brazil(UTC-5)'},
    {value:'UTC-4',name : 'Canada/Chili/Brazil(UTC-4)'},
    {value:'UTC-3',name : 'Argentina/Brazil(UTC-3)'},
    {value:'UTC-2',name : 'Brazil(UTC-2)'},
    {value:'UTC-1',name : 'Portugal(UTC-1)'},
    {value:'UTC+0',name : 'Island/Great Britain/Spain/Ghana(UTC+0)'},
    {value:'UTC+1',name : 'Europe/France/Spain/Poland(UTC+1)'},
    {value:'UTC+2',name : 'Finland/Russia/Israel(UTC+2)'},
    {value:'UTC+3',name : 'Russia/Belarus/Iraq(UTC+3)'},
    {value:'UTC+4',name : 'UAE/Armenia/Georgia(UTC+4)'},
    {value:'UTC+5',name : 'Kazakhstan/Tajikistan/Pakistan(UTC+5)'},
    {value:'UTC+6',name : 'Kyrgyzstan/Bangladesh(UTC+6)'},
    {value:'UTC+7',name : 'Mongolia/Thailand/Vietnam(UTC+7)'},
    {value:'UTC+8',name : 'Malaysia/Indonesia/Australia(UTC+8)'},
    {value:'UTC+9',name : 'Japan/Indonesia(UTC+9)'},
    {value:'UTC+10',name : 'Papua New Guinea/Australia(UTC+10)'},
    {value:'UTC+11',name : 'Russia/Solomon Islands/New Caledonia(UTC+11)'},
    {value:'UTC+12',name : 'Kiribati/Fiji/New Zealand(UTC+12)'}
];




