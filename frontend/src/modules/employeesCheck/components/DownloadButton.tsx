import React, {useState} from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {useTranslation} from "react-i18next";
import { buttonStyle } from '../../../common/styles';
import GetAppIcon from '@material-ui/icons/GetApp';

export default function DownloadButton({selected, download}: any) {

    const {t} = useTranslation();

    const [isDownloadButtonOpen, setIsDownloadButtonOpen] = useState(false);

    const handleClickOpen = () => {
        setIsDownloadButtonOpen(true);
    }

    const handleClose = () => {
        setIsDownloadButtonOpen(false);
    }

    const handleDownload = () => {
        if (download)
            download()
        handleClose();
    }


    return (
        <>
            <Button
                disabled={selected.length===0}
                style={buttonStyle}
                    variant="outlined"
                    color="primary"
                    onClick={handleClickOpen}>
                <GetAppIcon/>{t('Download')}</Button>

            <Dialog open={isDownloadButtonOpen} onClose={handleClose} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Подтвердите выбор</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {t('Are you sure you want to download this statistics?')}
                    </DialogContentText>

                </DialogContent>
                <DialogActions>
                    <Button style={buttonStyle} onClick={handleClose} color="primary">{t('Cancel')}</Button>
                    <Button style={buttonStyle} onClick={handleDownload} color="primary">{t('Download')}</Button>
                </DialogActions>
            </Dialog>

        </>
    );
}