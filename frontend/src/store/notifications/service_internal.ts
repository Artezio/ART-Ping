import { API_PREFIX } from "../../constants";
import * as config from "../../constants/firebase_config";
import { prepareFetchParams } from "../../store";
import { toast } from "react-toastify";

import { ShowNotification } from "./helpers";

import { receiveNotifications } from "./actions";

let notificationDebounce: any = null;

const requestToNotifications = (dispatch: any, isFirst: boolean = true) => {
  fetch(API_PREFIX + "/firebase/stub", prepareFetchParams())
    .then((responce: any) => {
      responce
        .json()
        .then((json: any) => {
          if (isFirst) {
            toast.success(`Connect to notifications... OK`, {
              pauseOnFocusLoss: false,
            });
            ShowNotification({
              text: "Connect to notifications... OK",
              sound: "jillys_sonar.wav",
              id: "connect-ok-sound-effect",
            });
          }
          dispatch(receiveNotifications(json));
        })
        .catch((error: any) => {
          console.error({
            point: "requestToNotifications:catch_1",
            error,
          });
        });
    })
    .catch((error) => {
      console.error({
        point: "requestToNotifications:catch_2",
        error,
      });
    });
};

const startWatcher = (dispatch: any) => {
  if (!notificationDebounce) {
    requestToNotifications(dispatch);
    notificationDebounce = setInterval(() => {
      requestToNotifications(dispatch, true);
    }, config.PING_NOTIFICATION_CONFIG.delay);
  }
};

export { startWatcher };
