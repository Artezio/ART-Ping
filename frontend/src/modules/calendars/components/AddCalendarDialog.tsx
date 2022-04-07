import Dialog from "@material-ui/core/Dialog";
import React from "react";
import { useTranslation } from "react-i18next";
import DialogTitle from "@material-ui/core/DialogTitle";
import { Formik } from 'formik';
import { ValidationSchema } from "./ValidationSchema";
import { EditorForm } from "./EditorForm";
import { DEFAULT_CALENDAR_PROPS } from '../../../constants';

export const AddCalendarDialog = ({
    open,
    onCancel,
    formSubmit
}: any) => {
    const { t } = useTranslation();
    return (
        <Dialog open={open} maxWidth={false}>
            <DialogTitle>{t('Create calendar')}</DialogTitle>
            <Formik
                initialValues={DEFAULT_CALENDAR_PROPS}
                validationSchema={ValidationSchema}
                onSubmit={(values, actions) => {
                    formSubmit(values);
                    actions.setSubmitting(false);
                }}
            >
                {props => <EditorForm isEdit={false} onCancel={onCancel} {...props} />}
            </Formik>
        </Dialog >
    );
};


