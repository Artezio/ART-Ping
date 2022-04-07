import React from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import {FormControlLabel, Checkbox, TextField, InputAdornment} from '@material-ui/core';
import {useTranslation} from "react-i18next";
import Button from '@material-ui/core/Button';
import {useDispatch} from 'react-redux';
import * as UserActions from '../../../store/user/actions';
import {useHistory} from "react-router";
import {TypedSelector} from "../../../store";
import Alert from "@material-ui/lab/Alert";
import {ValidationSchema} from "./ValidationSchema";
import {Formik, Form, Field} from "formik";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import VisibilityIcon from "@mui/icons-material/Visibility";

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
            backgroundColor: "#fff !important"
        },
        formButton: {
            width: "100%",
        },
        rememberMe: {
            display: "flex",
            whiteSpace: "nowrap",
            justifyContent: "space-between"
        },
        visibilityIcon: {
            color: "#76797F",
            cursor: "pointer",
        }
    })
);

const MainAuthForm = () => {
    const classes = useStyles();
    const dispatch = useDispatch();
    const {t} = useTranslation();
    const history = useHistory();
    const [formSettings, setFormSettings] = React.useState({
        rememberMe: false,
        submit: "Login"
    });

    const user = TypedSelector(state => state.user)

    const [passwordFieldVisibility, setPasswordFieldVisibility] = React.useState(false);

    const handleVisibilityChange = () => {
        setPasswordFieldVisibility(!passwordFieldVisibility)
    }

    const handleRememberMe = () => {
        setFormSettings(Object.assign({}, formSettings, {rememberMe: !formSettings.rememberMe}))
    };

    const handleSubmit = (values: any) => {
        dispatch(UserActions.TryAuthUser(Object.assign({}, formSettings, values)));
    }

    const handleForgotPassword = () => {
        history.push('/recovery-password');
    }

    return (
        <Formik
            initialValues={{
                login: '',
                password: '',
            }}
            onSubmit={values => handleSubmit(values)}
            validationSchema={ValidationSchema}
        >
            {(props: any) => (<Form onSubmit={props.handleSubmit}>
                <Alert
                    style={{
                        marginTop: "-30px",
                        visibility: (user.authStatus ? "visible" : "hidden"),
                    }}
                    severity="error">{user.authStatus ? "Неверные логин или пароль" : ""}</Alert>
                <Field
                    name="login"
                    className={classes.formControl}
                    render={({field, meta}: any) => {
                        return <TextField {...field}
                                          label={`${t('Login')}`}
                                          fullWidth
                                          helperText={meta.touched && meta.error}
                                          error={meta.touched && Boolean(meta.error)}
                        />
                    }}
                />
                <Field
                    name="password"
                    className={classes.formControl}
                    render={({field, meta}: any) => {
                        return <TextField {...field}
                                          type={passwordFieldVisibility ? 'text' : 'password'}
                                          label={`${t('Password')}`}
                                          fullWidth
                                          style={{backgroundColor: "#fff !important"}}
                                          InputProps={{
                                              endAdornment: (
                                                  <InputAdornment position="end"
                                                                  className={classes.visibilityIcon}
                                                                  onClick={handleVisibilityChange}
                                                  >
                                                      {passwordFieldVisibility ?
                                                          <VisibilityOffIcon/> : <VisibilityIcon/>}
                                                  </InputAdornment>
                                              )
                                          }}
                                          helperText={meta.touched && meta.error}
                                          error={meta.touched && Boolean(meta.error)}
                        />
                    }}
                />
                <div className={classes.rememberMe}>
                    <FormControlLabel
                        style={{fontSize: "12px"}}
                        control={
                            <Checkbox
                                color="primary"
                                checked={formSettings.rememberMe}
                                onChange={handleRememberMe}
                            />
                        } label={t("Remember me")}/>
                    <Button disableFocusRipple onClick={handleForgotPassword} disableRipple
                            style={{textTransform: "none", color: "#4A90E2"}}
                            variant="text"
                            color="primary">{t("Forgot password")}</Button>
                </div>
                <Button className={classes.formButton} type="submit" variant="contained"
                        color="primary">{t("Log in")}</Button>
            </Form>)}
        </Formik>
    );
}

export default MainAuthForm;

