import * as yup from "yup";
import { setLocale } from 'yup';
import i18next from "i18next";

setLocale({
    mixed: {
        required: () => i18next.t('Fill field'),
    },
});

export const ValidationSchema = yup.object().shape({
    id: yup.string(),
    name: yup.string().required(),
    workHoursFrom: yup.string().required(),
    workHoursTo: yup.string().required(),
    startWeekDay: yup.number(),
    events: yup.array(),
});
