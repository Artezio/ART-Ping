import { Reducer } from "redux";
import { ListActions, State } from "./types";
import {
  GET_PROJECTS_FAILURE,
  GET_PROJECTS_SUCCESS,
  SET_PROJECTS_LOADING,
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
    case SET_PROJECTS_LOADING:
      return {
        ...state,
        ui: {
          ...state.ui,
          isLoading: action.payload,
        },
      };
    case GET_PROJECTS_SUCCESS:
      return {
        ...state,
        data: {
          ...state.data,
          list: action.payload.list,
          total: action.payload.total,
        },
      };
    case GET_PROJECTS_FAILURE:
      return {
        ...state,
        data: {
          ...state.data,
          list: [],
          total: 0,
        },
      };

    default:
      return state;
  }
};

export default reducer;
