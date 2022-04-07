import * as firebase from "firebase/app";
import "firebase/messaging";
import {useHistory} from "react-router-dom";

import {toast} from "react-toastify";
import {API_PREFIX} from "../../constants";
import {prepareFetchParams} from "../../store";
import {FIREBASE_CONFIG, FIREBASE_LOCALSTORAGE_TOKEN_KEY,} from "../../constants/firebase_config";

import {receiveNotifications} from "./actions";

export enum TOKEN_ACTIONS {
  ADD = "add",
  DELETE = "delete",
}

export const updateSubscription = (token: string, action: TOKEN_ACTIONS) => {
  return fetch(
    API_PREFIX + "/subscription/" + action,
    prepareFetchParams({
      method: "POST",
      body: JSON.stringify({
        subscribeId: token,
      }),
    })
  )
    .then((responce: any) => {
      console.log({
        point: "updateSubscription:success",
        responce,
        token,
        action,
      });
    })
    .catch((error) => {
      console.error({
        point: "updateSubscription:error",
        error,
        token,
        action,
      });
    });
};

export const tokenStorage = {
  get() {
    try {
      const token = localStorage.getItem(FIREBASE_LOCALSTORAGE_TOKEN_KEY);
      return token && JSON.parse(token);
    } catch (error) {}
  },
  set(token: string) {
    localStorage.setItem(FIREBASE_LOCALSTORAGE_TOKEN_KEY, token);
  },
};

const StartWatcher = async (dispatch: any = null) => {
  let app = null;
  try {
    app = firebase.app();
  } catch (e) {
    app = firebase.initializeApp(FIREBASE_CONFIG);
  }
  const oldToken: string = tokenStorage.get();
  const messaging = firebase.messaging(app);

  if ("serviceWorker" in navigator) {
    navigator.serviceWorker
      .getRegistrations()
      .then((registredWorkers: readonly ServiceWorkerRegistration[]) => {
        const matchedWorkers = registredWorkers.filter(
          (worker: ServiceWorkerRegistration) => {
            return worker.scope === window.location.origin + "/";
          }
        );
        const registration = matchedWorkers[0];
        if (registration) {
          registration.addEventListener("updatefound", (data: any) => {
            console.log("updatefound", { data });
          });
          console.log("navigator.serviceWorker.register.", { registration });
        }
      })
      .catch((error: any) => {
        console.error("serviceWorker", error);
      });
  }
  messaging
    .getToken({ vapidKey: FIREBASE_CONFIG.vapidKey })
    .then((refreshedToken: string) => {
      if (oldToken !== refreshedToken) {
        oldToken ? updateSubscription(oldToken, TOKEN_ACTIONS.DELETE) : void 0;
        updateSubscription(refreshedToken, TOKEN_ACTIONS.ADD);
        tokenStorage.set(refreshedToken);
      }
    })
    .catch((error: any) => {
      toast.error(
        `Unable to retrieve refreshed firebase token: ${error.message}`
      );
      console.error("Unable to retrieve refreshed token", {
        error,
        messaging,
        oldToken,
      });
    });
  const history = useHistory();
  // Handle incoming messages. Called when:
  // - a message is received while the app has focus
  // - the user clicks on an app notification created by a service worker
  messaging.onMessage((payload: any) => {
    console.log("startWatcher.messaging.onMessage", { payload });
    dispatch(receiveNotifications(payload, history));
  });
};

export { StartWatcher };
