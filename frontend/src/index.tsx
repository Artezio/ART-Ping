import React from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, } from "react-router-dom";
import { createTheme, ThemeProvider } from '@material-ui/core/styles';
import { lightBlue, red } from '@material-ui/core/colors';
import { applyMiddleware, createStore } from 'redux';
import { Provider } from 'react-redux';
import thunk from 'redux-thunk';
import { reducer } from './store';
import './index.css';
import { I18nextProvider } from "react-i18next";
import { composeWithDevTools } from 'redux-devtools-extension';
import i18n from "./i18n";
import App from './App';
import DateFnsUtils from '@date-io/date-fns'
import { MuiPickersUtilsProvider } from "@material-ui/pickers"
import {ru} from "date-fns/locale";



const middleware = [thunk];

const store = createStore(reducer, composeWithDevTools(
    applyMiddleware(...middleware),
    // other store enhancers if any
));

// https://material-ui.com/customization/theming/
const theme = createTheme({
    palette: {
        primary: red,
    },
});

if ('serviceWorker' in navigator) {
    navigator.serviceWorker.getRegistrations()
        .then((registredWorkers: readonly ServiceWorkerRegistration[]) => {
            const matchedWorkers = registredWorkers.filter((worker: ServiceWorkerRegistration) => {
                console.log(window.location.origin + '/', worker)
                return worker.scope === window.location.origin + '/'
            });
            if (matchedWorkers.length < 1) {
                navigator.serviceWorker.register('/firebase-messaging-sw.js');
            }
        })
        .catch((error: any) => {
            console.error('serviceWorker', error)
        });
}

ReactDOM.render(
    <I18nextProvider i18n={i18n}>
        <Provider store={store}>
            <ThemeProvider theme={theme}>
                <MuiPickersUtilsProvider
                    utils={DateFnsUtils}
                    locale={ru}
                >
                    <Router>
                        <App />
                    </Router>
                </MuiPickersUtilsProvider>
            </ThemeProvider>
        </Provider>
    </I18nextProvider>,
    document.getElementById('root')
);
