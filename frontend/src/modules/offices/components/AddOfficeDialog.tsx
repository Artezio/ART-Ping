import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import React from "react";
import { Formik } from 'formik';
import EditorForm from "./EditorForm";
import { ValidationSchema } from "./ValidationSchema";
import { useTranslation } from "react-i18next";

export const AddOfficeDialog = ({
    editedData,
    calendars,
    open,
    onCancel,
    formSubmit,
}: any) => {


    const { t } = useTranslation();
    return (
        <Dialog open={open}>
            <DialogTitle>{t('Create office')}</DialogTitle>
            <Formik
                initialValues={editedData}
                validationSchema={ValidationSchema}
                onSubmit={(values, actions) => {
                    formSubmit(values);
                    actions.setSubmitting(false);
                }}
            >
                {props => <EditorForm isEdit={false} calendars={calendars} onCancel={onCancel} {...props} />}
            </Formik>

        </Dialog>
    );
};


