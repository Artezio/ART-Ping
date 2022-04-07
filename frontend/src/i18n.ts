import i18n from "i18next";
import {initReactI18next} from "react-i18next";
import ruTranslation from "./assets/localization/ru.json";

const resources = {
    ru: {
        translation: ruTranslation
    }
};

i18n
    .use(initReactI18next)
    .init({
        resources,
        fallbackLng: 'en',
        debug: false,
        lng: 'ru',
        cleanCode: true,
        saveMissing: true,
        missingKeyHandler: (lng, ns, key, fallbackValue) => {
            console.warn('missingKeyHandler',{ lng, ns, key, fallbackValue });
        },

        keySeparator: '.',

        interpolation: {
            escapeValue: true
        }
    });



export default i18n;