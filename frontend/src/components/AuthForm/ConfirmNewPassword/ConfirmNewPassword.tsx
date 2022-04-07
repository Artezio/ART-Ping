import React from 'react';
import {useParams} from 'react-router-dom';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import {Backdrop, CircularProgress, TextField, InputAdornment} from '@material-ui/core';
import {useTranslation} from "react-i18next";
import Button from '@material-ui/core/Button';
import {sendNewPassword} from "../actions";
import {ValidationSchema} from "./ValidationSchema";
import {Formik, Form, Field} from 'formik';
import {useHistory} from "react-router";
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import Alert from "@material-ui/lab/Alert";

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        formControl: {
            marginTop: 10,
            marginBottom: 10,
            width: "100%",
            display: "block"
        },
        formInput: {
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
            color: "#fff",
        },
        visibilityIcon: {
            color: "#76797F",
            cursor: "pointer",
        }
    })
);

const ConfirmNewPassword = () => {
    const params: { key: string } = useParams()
    const classes = useStyles();
    const {t} = useTranslation();
    const history = useHistory();
    const [requestState, setRequestState] = React.useState({
        sendingRequest: false,
    });
    const [passwordFieldVisibility, setPasswordFieldVisibility] = React.useState({
        passwordVisibility: false,
        confirmPasswordVisibility: false,
    });

    const [validationState, setValidationState] = React.useState({
        message: "",
    });

    const startRedirect = () => {
        history.push('/');
    }
    const handleVisibilityChange = (field: string) => {
        if (field === 'new password') {
            setPasswordFieldVisibility({
                ...passwordFieldVisibility,
                passwordVisibility: !passwordFieldVisibility.passwordVisibility
            })
        }
        if (field === 'confirm new password') {
            setPasswordFieldVisibility({
                ...passwordFieldVisibility,
                confirmPasswordVisibility: !passwordFieldVisibility.confirmPasswordVisibility
            })
        }
    }

    const submitingValidation = (errorMessage: string) => {
        if (errorMessage) {
            setValidationState({message: errorMessage})
        } else {
            setValidationState({message: ""})
        }
    }

    const handleSubmit = async (values: any) => {
        setRequestState({sendingRequest: true})
        await sendNewPassword(values?.password, params?.key, startRedirect);
        setRequestState({sendingRequest: false})
    }

    return (
        <div>
            <Alert
                style={{
                    marginTop: "-30px",
                    visibility: (validationState.message ? "visible" : "hidden"),
                }}
                severity="error">{validationState.message}</Alert>
            <Formik
                initialValues={{
                    password: "",
                    confirmPassword: "",
                }}
                onSubmit={e => handleSubmit(e)}
                validationSchema={ValidationSchema}
            >
                {(props: any) => (
                    <Form
                        onSubmit={props.handleSubmit}
                    >
                        <div className={classes.recoverHead}>
                            <h6 style={{fontWeight: "bold"}}>Сохранить новый пароль</h6>
                            <h2 style={{marginBottom: "20px"}}>Введите и подтвердите новый пароль</h2>
                        </div>
                        <Field
                            name="password"
                            style={{marginBottom: "20px"}}
                            render={({field, meta}: any) => {
                                return <TextField {...field}
                                                  className={classes.formInput}
                                                  type={passwordFieldVisibility.passwordVisibility ? 'text' : 'password'}
                                                  label={`${t('New password')}`}
                                                  fullWidth
                                                  InputProps={{
                                                      endAdornment: (
                                                          <InputAdornment
                                                              position="end"
                                                              className={classes.visibilityIcon}
                                                              onClick={() => handleVisibilityChange('new password')}
                                                          >
                                                              {passwordFieldVisibility.passwordVisibility ?
                                                                  <VisibilityOffIcon/> : <VisibilityIcon/>}
                                                          </InputAdornment>
                                                      )
                                                  }}
                                                  helperText={meta.touched && meta.error}
                                                  error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />
                        <Field
                            name="confirmPassword"
                            style={{marginBottom: "20px"}}
                            render={({field, meta}: any) => {
                                return <TextField {...field}
                                                  className={classes.formInput}
                                                  type={passwordFieldVisibility.confirmPasswordVisibility ? 'text' : 'password'}
                                                  label={`${t('Confirm new password')}`}
                                                  fullWidth
                                                  InputProps={{
                                                      endAdornment: (
                                                          <InputAdornment position="end"
                                                                          className={classes.visibilityIcon}
                                                                          onClick={() => handleVisibilityChange('confirm new password')}
                                                          >
                                                              {passwordFieldVisibility.confirmPasswordVisibility ?
                                                                  <VisibilityOffIcon/> : <VisibilityIcon/>}
                                                          </InputAdornment>
                                                      )
                                                  }}
                                                  helperText={meta.touched && meta.error}
                                                  error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />
                        <Button className={classes.formButton}
                                type="submit"
                                variant="contained"
                                onFocus={() => submitingValidation(props?.errors?.confirmPassword)}
                                color="primary">{t("Save new password")}</Button>
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

export default ConfirmNewPassword;

