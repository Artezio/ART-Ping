import React, { useState } from 'react';
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import { Formik } from 'formik';
import { EditorForm } from "./EditorForm";
import { ValidationSchema } from "./ValidationSchema";
import { getEntityById } from '../../../store/shared';
import { useTranslation } from "react-i18next";
import useChangeCalendarType from './hooks/useChangeCalendarType'
export const EditEmployeesDialog = ({
    editedData,
    open,
    onCancel,
    formSubmit
}: any) => {
    const { t } = useTranslation();

    const [entityData, setEntityData] = useState<any>({});
    const changeCalendarWrapper = useChangeCalendarType(setEntityData);
    const changeCalendarType = async (id: string, isCustom: boolean) => {
        await changeCalendarWrapper(id, isCustom, entityData);
    }

    if (editedData?.id && (editedData?.id !== entityData?.id)) {
        getEntityById(editedData.id, 'employee', (json: any) => {
            setEntityData(json);
        });
    }

    return (<>
        <Dialog
            open={open && (editedData?.id === entityData?.id)}
            maxWidth={false}>
            <DialogTitle>{!editedData.active ? t('View employee') : t('Edit employee')} <b>{entityData.firstName} {entityData.lastName}</b></DialogTitle>
            <Formik
                initialValues={entityData}
                validationSchema={ValidationSchema}
                enableReinitialize={true}
                onSubmit={(values, actions) => {
                    const updatedCallback = () => {
                        setEntityData({})
                    }
                    formSubmit(values, updatedCallback);
                    actions.setSubmitting(false);
                }}

            >
                {(props) => (<EditorForm isEdit={true} readOnly={!editedData.active}
                    onCancel={() => {
                        setEntityData({});
                        onCancel();
                    }}
                    onChange={async (id: string, isCustom: boolean, formValues: any) => {
                        await setEntityData(Object.assign(entityData, formValues))
                        await changeCalendarType(id, isCustom);
                    }}
                    {...props}
                />)}
            </Formik>
        </Dialog>
    </>);
};


