import { path } from "ramda";

import { NAMESPACE } from "./constants";

const dataPath: (...args: any) => any = (...args: any) =>
    path([NAMESPACE, ...args]);
const uiPath: (...args: any) => any = (...args: any) =>
    path([NAMESPACE, "ui", ...args]);

export const getSystems = dataPath("list");
// export const getTotal = dataPath("total");

export const isSystemsLoading = uiPath("isLoading");
