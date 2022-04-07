import { mergeEnvAndPreset } from './helpers';

export const FIREBASE_APP_NAME = 'art-ping-firebase-app';
const API_KEY = "AIzaSyB4A7JycTYI9vfMy17118QPrKJ2b_sKLLc";
const PROJECT_ID = "artpingdevcommtest";
const SENDER_ID = "384665351333";
const APP_ID = "1:384665351333:web:3ba595e86f48d95e40494c";
const MEASUREMENT_ID = "G-2RTZN13F4P";
const VAPIDKEY = "BE6bDMXUJfUXOFbrZDv8VQQboi76qwyE69aAsXV6UCVylzajcyhPgjlotJn8WwnqNA9BksGPkfEQzAdhzflytyA";

const DEFAULT_FIREBASE_CONFIG: any = {
    senderid: SENDER_ID,
    vapidKey: VAPIDKEY,
    apiKey: API_KEY,
    authDomain: `${PROJECT_ID}.firebaseapp.com`,
    databaseURL: `https://${PROJECT_ID}-default-rtdb.firebaseio.com`,
    projectId: PROJECT_ID,
    storageBucket: `${PROJECT_ID}.appspot.com`,
    messagingSenderId: SENDER_ID,
    appId: APP_ID,
    measurementId: `${MEASUREMENT_ID}`,
};

const DEFAULT_PING_NOTIFICATION_CONFIG = {
    delay: 1000 * 10, // 1 sec
    autoClose: 1000 * 20, // 20 sec
    audioElementDOMId: "audio_notifier"
};

export const PING_NOTIFICATION_CONFIG = mergeEnvAndPreset('REACT_APP_PING_NOTIFICATION_CONFIG_', DEFAULT_PING_NOTIFICATION_CONFIG);
//export const FIREBASE_CONFIG = mergeEnvAndPreset('REACT_APP_FIREBASE_CONFIG_', DEFAULT_FIREBASE_CONFIG);
export const FIREBASE_CONFIG = DEFAULT_FIREBASE_CONFIG;

export const FIREBASE_LOCALSTORAGE_TOKEN_KEY = process.env.REACT_APP_FIREBASE_TOKEN || "ARTPING_FIREBASE_TOKEN";
export const FIREBASE_PUSH_SERVER_KEY = process.env.REACT_APP_FIREBASE_PUSH_SERVER_KEY || "89GhTyAf90F4RmFRPeWxzwAzjon3MDhyWehHHGT";