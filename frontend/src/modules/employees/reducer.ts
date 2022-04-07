import { Reducer } from "redux";
import { ListActions, State } from "./types";
import {
  GET_EMPLOYEES_FAILURE,
  GET_EMPLOYEES_SUCCESS,
  SET_EMPLOYEES_LOADING,
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
    case SET_EMPLOYEES_LOADING:
      return {
        ...state,
        ui: {
          ...state.ui,
          isLoading: action.payload,
        },
      };
    case GET_EMPLOYEES_SUCCESS:
      return {
        ...state,
        data: {
          ...state.data,
          list: action.payload.list,
          total: action.payload.total,
        },
      };
    case GET_EMPLOYEES_FAILURE:
      return {
        ...state,
        ui: {
          ...state.ui,
          isLoading: action.payload,
        },
      };

    default:
      return state;
  }
};

export default reducer;
