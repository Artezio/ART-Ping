import {prepareFetchParams} from "../../store";
import {API_PREFIX} from "../../constants";
import {toast} from "react-toastify";

export const requestNotifications = async () => {
    const fetchParams = prepareFetchParams();
    try {
        const response: any = await fetch(API_PREFIX + '/notification/actual', fetchParams)
        if (response.ok) {
            const json = await response.json();
            return json;
        } else {
            throw new Error("Ошибка загрузки уведомлений");
        }
    } catch (error) {
        toast.error("Ошибка загрузки уведомлений");
        console.error({error});
    }

}

export const markReadNotification = async (id: string) => {
    const fetchParams = prepareFetchParams({
        method: "PATCH",
    });
    try {
        const response: any = await fetch(API_PREFIX + `/notification/${id}/markRead`, fetchParams)
        if (!response.ok) {
            throw new Error("Ошибка обновления статуса уведомлений");
        }
    } catch (error) {
        toast.error("Ошибка обновления статуса уведомлений");
        console.error({error});
    }
}
