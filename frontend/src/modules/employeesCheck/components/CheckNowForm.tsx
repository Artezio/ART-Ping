import React, {useState} from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {useTranslation} from "react-i18next";
import {buttonStyle} from '../../../common/styles';
import Notifications from '@material-ui/icons/Notifications';
import {useSelector} from "react-redux";
import {hasAccessCheckNowSelector} from "../selectors";
import {DialogConfirm} from "../../../common/dialogs/DialogConfirm";
import {DateInterface, EmployeeCheckInterface} from "../types";
import {format} from "date-fns";


export default function CheckNowForm({checkNow, disabled, employeeChecks = [], selected}: any) {
    const hasAccessToCheckNow = useSelector(hasAccessCheckNowSelector);
    const [isConfirmOpen, setIsConfirmOpen] = useState<boolean>(false);
    const {t} = useTranslation();

    const [isCheckNowFormOpen, setIsCheckNowFormOpen] = useState(false);

    const handleClickOpen = () => {
        setIsCheckNowFormOpen(true);
    }

    const handleClose = () => {
        setIsCheckNowFormOpen(false);
    }
// 1 - фильтр по селектнутым,
//         2- файед

    function getEmployeeWithHoliday(employeeChecks: Array<EmployeeCheckInterface>, selected: Array<string>) {

        const employeeChecksSelected = employeeChecks.filter((employeeCheck) => selected.indexOf(employeeCheck.id) !== -1);
        const nowAsStr = (format(new Date(), 'yyy-MM-dd'))
        return employeeChecksSelected.find((employeeCheck) => {
            return employeeCheck.dates.find((date: DateInterface) => {
                return date.date === nowAsStr && ((date.type) === "WEEKEND" || (date.type) === "HOLIDAY");
            })
        })
        // return employeeChecks.find((employeeCheck: any) => {
        //     const onlyToday = employeeCheck.filter((date: any) => date === new Date());
        //     const markHoliday = !!employeeCheck.dates.find((date: any) => (date.type) === "WEEKEND" || (date.type) === "HOLIDAY");
        //
        //     if (selected.indexOf(employeeCheck.id) !== -1) {
        //
        //         return markHoliday;
        //
        //     } else {
        //         return false;
        //     }
        // });
    }

    function check() {
        if (checkNow)
            checkNow()
        handleClose();
    }

    const handleCheckNow = () => {

        if (getEmployeeWithHoliday(employeeChecks, selected)) {
            setIsConfirmOpen(true)

        } else {
            check();
        }
    }

    return (
        <>
            <Button style={buttonStyle} variant="outlined" color="primary" disabled={disabled || !hasAccessToCheckNow}
                    onClick={handleClickOpen}><Notifications/>{t('Check now')}</Button>

            <Dialog open={isCheckNowFormOpen} onClose={handleClose} aria-labelledby="form-dialog-title">
                {/*    if(){*/}
                {/*    return toast()*/}
                {/*}*/}
                <DialogTitle id="form-dialog-title">Подтвердите выбор</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {t('Are you sure you want to check the data of employees?')}
                    </DialogContentText>
                    <DialogConfirm
                        open={isConfirmOpen}
                        message={'Для сотрудников текущий день является выходным или праздничным. Вы действительно хотите запустить проверку?'}
                        onYesClick={() => {
                            setIsConfirmOpen(false);
                            check();
                        }}
                        onNoClick={() => {
                            setIsConfirmOpen(false)
                            handleClose();
                        }}/>
                </DialogContent>
                <DialogActions>
                    <Button style={buttonStyle} onClick={handleClose} color="primary">{t('Cancel')}</Button>
                    <Button style={buttonStyle} onClick={handleCheckNow} color="primary">{t('Check now')}</Button>
                </DialogActions>
            </Dialog>

        </>
    );
}
