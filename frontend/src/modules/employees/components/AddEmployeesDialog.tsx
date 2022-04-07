import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import React, {useState} from "react";
import { Formik, useFormikContext } from 'formik';
import { EditorForm } from "./EditorForm";
import { ValidationSchema } from "./ValidationSchema";
import { DEFAULT_USER_PROPS } from '../../../constants';
import { useTranslation } from "react-i18next";
import useChangeCalendarType from './hooks/useChangeCalendarType'

export const AddUserDialog = ({
                                  editedData,
                                  offices,
                                  calendars,
                                  projects,
                                  open,
                                  onCancel,
                                  formSubmit
                              }: any) => {

    const { t } = useTranslation();

    const [entityData, setEntityData] = useState<any>({ ...DEFAULT_USER_PROPS });
    const changeCalendarWrapper = useChangeCalendarType(setEntityData);
    const changeCalendarType = async (id: string, isCustom: boolean) => {
        await changeCalendarWrapper(id, isCustom, entityData);
    }

    return (
        <Dialog
            maxWidth={false}
            open={open}>
            <DialogTitle>{t('Create employee')}</DialogTitle>
            <Formik
                initialValues={ entityData }
                validationSchema={ ValidationSchema }
                enableReinitialize={ true }
                onSubmit={(values, actions) => {
                    formSubmit(values);
                    actions.setSubmitting(false);
                }}

            >
                {props => <EditorForm
                    onCancel={ () => {
                        setEntityData({ ...DEFAULT_USER_PROPS });
                        onCancel();
                    }
                    }
                    onChange={async (id: string, isCustom: boolean, formValues: any) => {
                        await setEntityData(Object.assign(entityData, formValues))
                        await changeCalendarType(id, isCustom);
                    }}
                    {...props} />}
            </Formik>

        </Dialog>
    );
};


