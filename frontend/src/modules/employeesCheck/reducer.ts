import { Reducer } from "redux";
import { ListActions, State } from "./types";
import {
  FILTER_EMPLOYEE_CHECKS_FAILURE,
  FILTER_EMPLOYEE_CHECKS_SUCCESS,
  FILTER_EMPLOYEE_CHECKS_LOADING,
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
    case FILTER_EMPLOYEE_CHECKS_LOADING:
      return {
        ...state,
        ui: {
          ...state.ui,
          isLoading: action.payload,
        },
      };
    case FILTER_EMPLOYEE_CHECKS_SUCCESS:
      return {
        ...state,
        data: {
          ...state.data,
          list: action.payload.list,
          total: action.payload.total,
        },
      };
    case FILTER_EMPLOYEE_CHECKS_FAILURE:
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
