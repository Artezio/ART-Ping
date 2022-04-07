import React, { useState } from 'react';
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import { Formik } from 'formik';
import EditorForm from "./EditorForm";
import { ValidationSchema } from "./ValidationSchema";
import { getEntityById } from '../../../store/shared';
import { useTranslation } from "react-i18next";

export const EditProjectDialog = ({
    editedData,
    open,
    onCancel,
    formSubmit,
    isOnlyView
}: any) => {
    const { t } = useTranslation();

    const [entityData, setEntityData] = useState<any>({});
    if (editedData?.id && (editedData?.id !== entityData?.id)) {
        getEntityById(editedData.id, 'projects', (data: any) => {
            setEntityData(data);
        });
    }

    return (
        <Dialog
            open={open && (editedData?.id === entityData?.id)}
            maxWidth={false}>
            <DialogTitle>{isOnlyView ? t('View project') : t('Edit project')}</DialogTitle>
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
                    onCancel={() => {
                        setEntityData({});
                        onCancel();
                    }}  {...props}
                />}
            </Formik>

        </Dialog>
    );
};


