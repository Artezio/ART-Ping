import { API_PREFIX, NOTIFICATION_TRANSPORT } from "../../constants";
import * as config from "../../constants/firebase_config";
import { prepareFetchParams } from "../shared";
import { toast } from "react-toastify";
import { ActionCreator } from "redux";
import * as Internal from "./service_internal";
import * as Firebase from "./service_firebase";
import {
  ReceiveNotificationsAction,
  AnswerNotificationsAction,
  GET_NOTIFICATIONS,
  RECEIVE_NOTIFICATIONS,
  CLICK_NOTIFICATION,
  ANSWER_NOTIFICATION,
} from "./types";

import { ShowNotification } from "./helpers";
import createAction from "../../common/utils/createAction";

export const PING_STATUS = {
  IN_PROGRESS: "IN_PROGRESS",
  NOT_SUCCESS: "NOT_SUCCESS",
  NO_RESPONSE: "NO_RESPONSE",
  SUCCESS: "SUCCESS",
};

const setLastPingStatus: ActionCreator<AnswerNotificationsAction> = (
  payload: any
) => {
  toast.success(`Отлично!`, {
    pauseOnFocusLoss: false,
    autoClose: config.PING_NOTIFICATION_CONFIG.autoClose,
  });
  return {
    type: ANSWER_NOTIFICATION,
    payload: payload,
  };
};

const getGeoPosition = () => {
  return new Promise((resolve, reject) => {
    navigator.geolocation.getCurrentPosition(resolve, reject, {maximumAge: 0, timeout: 5000, enableHighAccuracy: true});
  });
}

const pingAnswer = async (dispatch: any, notificationsId: string | number) => {
  const geoPosition = await getGeoPosition()
      .then((value: any)=> {
        return {
          accuracy: value.coords.accuracy || 0,
          latitude: value.coords.latitude || 0,
          longitude: value.coords.longitude || 0,
          valid: true
        }
      })
      .catch(() => {
        return {
          accuracy: 0,
          latitude: 0,
          longitude: 0,
          valid: false,
        }
      }
  );

  fetch(
    API_PREFIX + "/iamhere/check",
    prepareFetchParams({
      method: "PUT",
      body: JSON.stringify(geoPosition),
    })
  )
    .then((responce: any) => {
      responce
        .text()
        .then((resp: any) => {
          dispatch(setLastPingStatus(resp));
        })
        .catch((error: any) => {
          toast.warn(`Что-то не так...`, {
            pauseOnFocusLoss: false,
            autoClose: config.PING_NOTIFICATION_CONFIG.autoClose,
          });
          console.error({
            point: "pingAnswer:catch_1",
            notificationsId,
            error,
          });
        });
    })
    .catch((error) => {
      console.error({
        point: "pingAnswer:catch_2",
        notificationsId,
        error,
      });
    });
};

export const InitNotifications = () => {
  return (dispatch: any) => {
    switch (NOTIFICATION_TRANSPORT) {
      case "firebase":
        Firebase.StartWatcher(dispatch);
        break;
      case "internalhttp":
        Internal.startWatcher(dispatch);
        break;
    }

    return {
      type: GET_NOTIFICATIONS,
    };
  };
};

export const OnPingAccepted = (testId: string) => {
  return (dispatch: any) => {
    pingAnswer(dispatch, testId);
    return {
      type: GET_NOTIFICATIONS,
    };
  };
};

export const pingToastClicked = (payload: any) =>
  createAction(CLICK_NOTIFICATION, payload);

export const receiveNotifications = (payload: any, history: any = null) => {
  return async (dispatch: any) => {
    console.log({ payload });
    // if (payload.status === PING_STATUS.IN_PROGRESS)
    window.focus();
    // всплывающее окно на самой странице приложения 
    toast.warn(`Есть кто на месте?`, {
      pauseOnFocusLoss: false,
      autoClose: false,
      onClick: () => {
        dispatch(
          pingToastClicked({
            url: "/?ping=1",
            open: true,
          })
        );
      },
    });
    ShowNotification(
      {
        text: "PING!",
        sound: "jillys_sonar.mp3",
        url: "/?ping=1",
      },
      history
    );

    return {
      type: RECEIVE_NOTIFICATIONS,
      payload,
    };
  // }
};
};
