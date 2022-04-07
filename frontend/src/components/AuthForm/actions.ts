import {API_PREFIX} from "../../constants";
import {prepareFetchParams} from "../../store";
import {toast} from "react-toastify";

export const sendReqRecoverPassword = async (email: string) => {
    try {
        const response = await fetch(
            `${API_PREFIX}/recovery/init`,
            prepareFetchParams({
                method: "POST",
                body: JSON.stringify(email),
            })
        );
        if (response.ok) {
            return true;
        } else {
            const message = await response.json();
            throw new Error(`${message.message}`);
            return false;
        }
    } catch (error) {
        console.log({error}.error);
        toast.error(`${{error}.error}`);
        return false;
    }
};
export const sendNewPassword = async (newPassword: string, key: string, startRedirect: () => void) => {
    const encodedPassword = btoa(newPassword)
    try {
        const response = await fetch(
            `${API_PREFIX}/recovery/${key}`,
            prepareFetchParams({
                method: "PUT",
                body: JSON.stringify(encodedPassword),
            })
        );

        if (response.ok) {
            toast.success(`Пароль изменен`);
            startRedirect();
        } else {
            const message = await response.json();
            throw new Error(`${message.message}`);
        }
    } catch (error) {
        console.log({error}.error);
        toast.error(`${{error}.error}`);
    }
};
