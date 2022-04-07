import React from "react";
import {Form} from 'formik';
import {CombinedFormComponent} from "../../../components/misc/Calendar/CombinedFormComponent";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import {buttonStyle} from "../../../common/styles";


const useStyles = makeStyles((theme: Theme): { root: any } =>
    createStyles({
        root: {},
    }),
);


export const EditorForm = (props: any) => {
    const classes = useStyles();
    const {t} = useTranslation();
    const {
        onCancel,
        handleSubmit,
        isValid,
        isEdit
    } = props;

    return (
        <Form
            className={classes.root}
            onSubmit={handleSubmit}
        >
            <DialogContent style={{display: "flex", overflow:"hidden"}}>
                <CombinedFormComponent
                    withName={true}
                />
            </DialogContent>
            <DialogActions>
                <Button
                    onClick={() => {
                        onCancel();
                    }}
                    color="primary"
                >
                    {t('Cancel')}
                </Button>
                <Button style={buttonStyle} type="submit" color="secondary" variant="contained" disabled={!isValid}>
                    {isEdit ? t('Save') : t('Add')}
                </Button>
            </DialogActions>
        </Form>
    );
};
