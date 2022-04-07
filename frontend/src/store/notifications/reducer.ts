import { Reducer } from "redux";
import {
  NotificationsActions,
  GET_NOTIFICATIONS,
  LOAD_NOTIFICATIONS,
  RECEIVE_NOTIFICATIONS,
  ANSWER_NOTIFICATION,
  CLICK_NOTIFICATION,
} from "./types";
import * as NotificationsActionsTypes from "./actions";

export const initialState: any = {
  test: null,
  started: false,
};

export const reducer: Reducer<any> = (state: any = initialState, action) => {
  const newState = { ...state };
  switch ((action as NotificationsActions).type) {
    case GET_NOTIFICATIONS:
      newState.test = null;
      newState.started = true;
      break;

    case LOAD_NOTIFICATIONS:
      break;

    case RECEIVE_NOTIFICATIONS:
      newState.test = action.payload;
      newState.started = true;
      break;
    case CLICK_NOTIFICATION:
      newState.isConfirmPingOpen = action.payload.open;
      newState.url = action.payload.url;
      newState.test = {
        status: NotificationsActionsTypes.PING_STATUS.IN_PROGRESS,
      };
      break;
    case ANSWER_NOTIFICATION:
      newState.test = action.payload;
      newState.started = true;
      break;

    default:
      break;
  }
  return newState;
};

export default reducer;
