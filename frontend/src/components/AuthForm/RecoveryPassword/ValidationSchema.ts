import * as yup from "yup";
import {setLocale} from 'yup';
import i18next from "i18next";

setLocale({
    mixed: {
        required: () => i18next.t('Fill field'),
    },
    string: {
        email: () => i18next.t('Email format not valid'),
    }
});

export const ValidationSchema = yup.object().shape({
    email: yup.string().email().required(),
});
