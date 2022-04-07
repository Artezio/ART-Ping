import * as yup from "yup";
import { setLocale } from 'yup';
import i18next from "i18next";
setLocale({
    mixed: {
        required: () => i18next.t('Fill field'),
        notType: () => i18next.t('Must be number type'),
    },
    number: {
        positive: () => i18next.t('Must be positive'),
    },
});

export const ValidationSchema = yup.object().shape({
    recommendedDailyChecks: yup.number().positive().required(),
    testNoResponseMinutes: yup.number().positive().required(),
    testSuccessfulMinutes: yup.number().positive().required(),
});

