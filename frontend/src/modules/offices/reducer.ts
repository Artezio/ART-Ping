import { Reducer } from "redux";
import { ListActions, State } from "./types";
import {
  GET_OFFICES_FAILURE,
  GET_OFFICES_SUCCESS,
  SET_OFFICES_LOADING,
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
    case SET_OFFICES_LOADING:
      return {
        ...state,
        ui: {
          ...state.ui,
          isLoading: action.payload,
        },
      };
    case GET_OFFICES_SUCCESS:
      return {
        ...state,
        data: {
          ...state.data,
          list: action.payload.list,
          total: action.payload.total,
        },
      };
    case GET_OFFICES_FAILURE:
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
