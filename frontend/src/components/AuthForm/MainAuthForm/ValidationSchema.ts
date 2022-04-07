import * as yup from "yup";
import {setLocale} from 'yup';
import i18next from "i18next";

setLocale({
    mixed: {
        required: () => i18next.t('Fill field'),
    },
});

export const ValidationSchema = yup.object().shape({
    login: yup.string().required(),
    password: yup.string().required(),
});
