import React, { useState } from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import { Formik } from 'formik';
import EditorForm from "./EditorForm";
import { ValidationSchema } from "./ValidationSchema";
import { getEntityById } from '../../../store/shared';
import { useTranslation } from "react-i18next";

export const EditOfficeDialog = ({
    editedData,
    open,
    onCancel,
    formSubmit,
    calendars,
    isOnlyView
}: any) => {

    const { t } = useTranslation();

    const [entityData, setEntityData] = useState<any>({});

    if (editedData?.id && (editedData?.id !== entityData?.id)) {
        getEntityById(editedData.id, 'offices', (data: any) => {
            setEntityData(data);
        });
    }

    return (
        <Dialog
            open={open && (editedData?.id === entityData?.id)}
            maxWidth={false}>
            <DialogTitle>{isOnlyView ? t('View office') : t('Edit office')}</DialogTitle>
            <Formik
                initialValues={entityData}
                validationSchema={ValidationSchema}
                onSubmit={(values, actions) => {
                    const updatedCallback = () => {
                        setEntityData({})
                    }
                    formSubmit(values, updatedCallback);
                    actions.setSubmitting(false);
                }}

            >
                {props => <EditorForm
                    isEdit={true}
                    calendars={calendars}
                    onCancel={() => {
                        setEntityData({});
                        onCancel();
                    }} {...props}
                />}
            </Formik>

        </Dialog>
    );
};


