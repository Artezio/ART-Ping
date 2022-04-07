import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import React from "react";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import { buttonStyle } from "../styles";

const useStyles = makeStyles((theme: Theme): { dialogConfirm: any } =>
    createStyles({
        dialogConfirm: {}
    })
);

export const DialogConfirm = ({
                                  onYesClick,
                                  onNoClick,
                                  open=false,
                                  message = null,
                                  title = null
                              }: any) => {
    const classes = useStyles();
    const {t} = useTranslation();

    return <Dialog className={classes.dialogConfirm}
                   open={open}
    >
        <DialogTitle>{title || t('Confirmation')}</DialogTitle>
        <DialogContent>
            {message || t('Are you sure?')}
        </DialogContent>
        <DialogActions>
            <Button style={buttonStyle} onClick={onYesClick} color="primary">{t('Yes')}</Button>
            <Button style={buttonStyle} onClick={onNoClick} color="primary">{t('No')}</Button>
        </DialogActions>
    </Dialog>;
}