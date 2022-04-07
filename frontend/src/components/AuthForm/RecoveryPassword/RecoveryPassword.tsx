import React from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import {Backdrop, CircularProgress, TextField} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import {useTranslation} from "react-i18next";
import Button from '@material-ui/core/Button';
import {sendReqRecoverPassword} from "../actions";
import {ValidationSchema} from "./ValidationSchema";
import {Field, Form, Formik} from "formik";

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        formControl: {
            marginTop: 10,
            marginBottom: 10,
            width: "100%",
            display: "block"
        },
        formInput: {
            width: "100%",
            marginBottom: "20px",
        },
        formButton: {
            width: "100%",
        },
        recoverHead: {
            display: "flex",
            flexDirection: "column",
            fontSize: "12px",
            textAlign: "center",
        },
        backdrop: {
            zIndex: theme.zIndex.drawer + 1,
            color: '#fff',
        },
    })
);

const RecoveryPassword = () => {
    const classes = useStyles();
    const {t} = useTranslation();
    const [requestState, setRequestState] = React.useState({
        sendingRequest: false,
        requestSuccess: false,
    });

    const handleSubmit = async (value: any) => {
        setRequestState({...requestState, sendingRequest: true})
        const isSuccess = await sendReqRecoverPassword(value);
        if (isSuccess) {
            setRequestState({sendingRequest: false, requestSuccess: true})
        } else {
            setRequestState({...requestState, sendingRequest: false})
        }
    }

    return (
        <div>
            <Alert
                style={{
                    marginTop: "-30px",
                    visibility: (requestState.requestSuccess ? "visible" : "hidden"),
                }}
                severity="success">В ближайшее время вы должны получить письмо с инструкциями</Alert>
            <Formik
                initialValues={{
                    email: "",
                }}
                onSubmit={e => handleSubmit(e)}
                validationSchema={ValidationSchema}
            >
                {(props: any) => (
                    <Form
                        onSubmit={props.handleSubmit}
                    >
                        <div className={classes.recoverHead}>
                            <h6 style={{fontWeight: "bold"}}>Сбросить пароль</h6>
                            <h2 style={{marginBottom: "20px"}}>Введите свой e-mail для восстановления пароля</h2>
                        </div>
                        <Field
                            name="email"
                            style={{marginBottom: "20px"}}
                            render={({field, meta}: any) => {
                                return <TextField {...field}
                                                  className={classes.formInput}
                                                  label={`${t('Email')}`}
                                                  fullWidth
                                                  helperText={meta.touched && meta.error}
                                                  error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />
                        <Button className={classes.formButton}
                                type="submit"
                                variant="contained"
                                color="primary">{t("Recover password")}</Button>
                    </Form>
                )}
            </Formik>
            <Backdrop
                className={classes.backdrop}
                open={requestState.sendingRequest}
            >
                <CircularProgress color="inherit"/>
            </Backdrop>

        </div>
    );
}

export default RecoveryPassword;

