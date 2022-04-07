import React, {useState} from 'react';

import {useTranslation} from "react-i18next";

import Button from '@material-ui/core/Button';

import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import TextField from '@material-ui/core/TextField';
import {Field, Formik} from "formik";
import {buttonStyle} from '../../../common/styles';
import {useSelector} from "react-redux";
import {hasAccessSetAutoCheckSelector} from "../selectors";


const AutoCheckForm = ({setAutoCheck, disabled}: any) => {
    const hasAccessToSetAutoCheck = useSelector(hasAccessSetAutoCheckSelector);

    const {t} = useTranslation();

    const [isAutoCheckDialogOpen, setIsAutoCheckDialogOpen] = useState(false);

    const openAutoCheckDialog = () => {
        setIsAutoCheckDialogOpen(true);
    }

    const handleClose = () => {
        setIsAutoCheckDialogOpen(false);
    }

    return <>
        <Button style={buttonStyle} variant="outlined" color="primary" disabled={disabled || !hasAccessToSetAutoCheck}
                onClick={openAutoCheckDialog}>{t('Set autoCheck')}</Button>
        <div style={{flexGrow: 1}}/>

        <Dialog open={isAutoCheckDialogOpen} onClose={handleClose} aria-labelledby="form-dialog-title">
            <DialogTitle id="form-dialog-title">{t('Set autoCheck')}</DialogTitle>
            <Formik
                initialValues={{dailyChecks: 0}}
                onSubmit={(values, actions) => {
                    setAutoCheck(values);
                    actions.setSubmitting(false);
                    handleClose();
                }}

            >
                {({handleSubmit}) => <form onSubmit={handleSubmit}>
                    <DialogContent>
                        <Field
                            name="dailyChecks"
                            render={({field, meta}: any) => {
                                return <TextField {...field}
                                                  label={t("Checks number")}
                                                  fullWidth
                                                  input="number"
                                                  InputProps={{inputProps: {min: 0}}}
                                                  helperText={meta.touched && meta.error}
                                                  error={meta.touched && Boolean(meta.error)}
                                />
                            }}

                        />
                    </DialogContent>

                    <DialogActions>

                        <Button style={buttonStyle} variant="contained" onClick={handleClose}
                                color="primary">{t('Cancel')}</Button>

                        <Button style={buttonStyle} variant="contained" type="submit"
                                color="primary">{t('Confirm')}</Button>
                    </DialogActions>
                </form>
                }
            </Formik>

        </Dialog>
    </>
};
export default AutoCheckForm;

