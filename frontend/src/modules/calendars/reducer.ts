import { Reducer } from "redux";
import { ListActions, State } from "./types";
import {
  GET_CALENDARS_FAILURE,
  GET_CALENDARS_SUCCESS,
  SET_CALENDARS_LOADING,
} from "./constants";

export const initialState: State = {
  ui: {
    isLoading: false,
  },
  data: {
    list: [],
    total: 0,
  },
};

export const reducer: Reducer<State> = (
  state: State = initialState,
  action
) => {
  switch ((action as ListActions).type) {
    case SET_CALENDARS_LOADING:
      return {
        ...state,
        ui: {
          ...state.ui,
          isLoading: action.payload,
        },
      };
    case GET_CALENDARS_SUCCESS:
      return {
        ...state,
        data: {
          ...state.data,
          list: action.payload.list,
          total: action.payload.total,
        },
      };
    case GET_CALENDARS_FAILURE:
      return {
        ...state,
        data: {
          ...state.data,
          list: [],
        },
      };

    default:
      return state;
  }
};

export default reducer;
