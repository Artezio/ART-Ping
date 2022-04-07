import Paper from '@material-ui/core/Paper';
import Button from "@material-ui/core/Button";
import { useTranslation } from "react-i18next";
import { buttonStyle } from '../../common/styles';

export const NotificationSetupDialog = () => {
    const { t } = useTranslation();

    const paperStyle: any = { margin: 'auto', marginTop: 50, width: 500, textAlign: 'center', padding: 10 };
    const h1Style: any = { marginTop: 20, fontSize: '120%' };
    const imgStyle: any = { margin: 'auto', width: 300, };
    
    return <Paper style={paperStyle}>
        <h1 style={{fontSize: '120%'}}>1. {t('Allow display notification')}</h1>
        <img src='/alert.f0c4213a.png' style={imgStyle} />
        <Button style={buttonStyle} onClick={() => { Notification.requestPermission() }} color="primary">{t('Allow notifications')}</Button>

        <h1 style={h1Style}>2. {t('Allow browser background mode')}</h1>
        <img src='/browser-config.4425650b.png' style={imgStyle} />

        <h1 style={h1Style}>3. {t('Stay tuned')}</h1>
        <Button style={buttonStyle} onClick={() => { window.location.reload() }} color="primary" >{t('Done')}</Button>
    </Paper>
}