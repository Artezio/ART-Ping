import { path } from "ramda";

import { NAMESPACE } from "./constants";

const dataPath: (...args: any) => any = (...args: any) =>
  path([NAMESPACE, "data", ...args]);
const uiPath: (...args: any) => any = (...args: any) =>
  path([NAMESPACE, "ui", ...args]);

export const getProjects = dataPath("list");
export const getTotal = dataPath("total");
export const isProjectsLoading = uiPath("isLoading");
