import { Action } from "redux";

export const GET_NOTIFICATIONS = "@@notifications/GET_NOTIFICATIONS";
export const LOAD_NOTIFICATIONS = "@@notifications/LOAD_NOTIFICATIONS";
export const RECEIVE_NOTIFICATIONS = "@@notifications/RECEIVE_NOTIFICATIONS";
export const ANSWER_NOTIFICATION = "@@notifications/ANSWER_NOTIFICATION";
export const CLICK_NOTIFICATION = "@@notifications/CLICK_NOTIFICATION";

export interface NotificationsInterface {
  id: string;
  startTime: string; // 2021-08-19T12:43:54.313Z
  status: string; // [IN_PROGRESS|...]
  duration: number; // seconds
}

export interface GetNotificationsAction extends Action {
  type: typeof GET_NOTIFICATIONS;
}

export interface LoadNotificationsAction extends Action {
  type: typeof LOAD_NOTIFICATIONS;
}

export interface ReceiveNotificationsAction extends Action {
  type: typeof RECEIVE_NOTIFICATIONS;
  payload: any;
}

export interface ClickNotificationsAction extends Action {
  type: typeof CLICK_NOTIFICATION;
  payload: any;
}

export interface AnswerNotificationsAction extends Action {
  type: typeof ANSWER_NOTIFICATION;
  payload?: any;
}

export type NotificationsActions =
  | GetNotificationsAction
  | LoadNotificationsAction
  | ReceiveNotificationsAction
  | ClickNotificationsAction
  | AnswerNotificationsAction;
