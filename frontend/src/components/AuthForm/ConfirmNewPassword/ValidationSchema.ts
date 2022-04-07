import * as yup from "yup";
import {setLocale} from 'yup';
import i18next from "i18next";

setLocale({
    mixed: {
        required: () => i18next.t('Fill field'),
    },
});

export const ValidationSchema = yup.object().shape({
    password: yup.string().required(),
    confirmPassword: yup.string().required().oneOf([yup.ref('password'), null], i18next.t('Passwords must match')),
});
