export default function createAction(
  type: string,
  payload: any = {},
  meta: {} = {}
): {} {
  return {
    type,
    payload,
    meta
  };
}
