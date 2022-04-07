import * as yup from "yup";
import { setLocale } from 'yup';
import i18next from "i18next";

setLocale({
    mixed: {
        required: () => i18next.t('Fill field'),
    },
});

export const ValidationSchema = yup.object().shape({
    name: yup.string().required(),
    calendarId: yup.string().required()
});

