import * as React from 'react';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import {useTranslation} from "react-i18next";
import {useHistory} from "react-router-dom";
import {buttonStyle} from '../../../../common/styles';
import {useSelector} from "react-redux";
import {
    hasAccessToPageCalendarsSelector,
    hasAccessToPageEmployeesSelector,
    hasAccessToPageOfficesSelector,
    hasAccessToPageProjectsSelector,
    hasAccessToPageSystemSettingsSelector,
    hasAccessToPageDataImportSelector,
} from "../../../../modules/employeesCheck/selectors";
import {getAdminItemSeeds} from "../AdminMenuItemsSeeds";
import {useMemo} from "react";

interface IBasicCardProps {
    title?: string;
    path?: string;
    icon?: any;
    description?: string;
    disabled?: boolean;
}

const BasicCard = (props: IBasicCardProps) => {
    const history = useHistory();
    const {t} = useTranslation();


    const title = props.title ? t(props.title) : "__";

    return (
        <Card style={{display: 'inline-block', margin: 20, width: 350}}>
            <CardContent>
                <Typography variant="h5"
                            style={{height: 30}}
                            component="div">{props.icon} {title}</Typography>
                <Typography
                    style={{height: 50}}
                    component="div">{t(props.description || "")}.</Typography>
                <Button
                    style={buttonStyle}
                    variant="outlined"
                    color="primary"
                    disabled={props.disabled}
                    onClick={() => {
                        props.path ? history.push(props.path) : void (0);
                    }}>{t('Open')}</Button>
            </CardContent>
        </Card>
    );
}

const Dashboard = () => {
    const hasAccessToPageSystemSettings = useSelector(hasAccessToPageSystemSettingsSelector);
    const hasAccessToPageProjects = useSelector(hasAccessToPageProjectsSelector);
    const hasAccessToPageCalendars = useSelector(hasAccessToPageCalendarsSelector);
    const hasAccessToPageOffices = useSelector(hasAccessToPageOfficesSelector);
    const hasAccessToPageEmployees = useSelector(hasAccessToPageEmployeesSelector);
    const hasAccessToPageDataImport = useSelector(hasAccessToPageDataImportSelector);
    const iconStyle: any = {}
    const seeds = useMemo(() => getAdminItemSeeds(
        hasAccessToPageSystemSettings,
        hasAccessToPageProjects,
        hasAccessToPageCalendars,
        hasAccessToPageOffices,
        hasAccessToPageEmployees,
        hasAccessToPageDataImport,
        iconStyle
    ), [hasAccessToPageSystemSettings, hasAccessToPageProjects, hasAccessToPageCalendars,
        hasAccessToPageOffices, hasAccessToPageEmployees, hasAccessToPageDataImport, iconStyle]);

    let buttons: any = [];

    for (let key in seeds) {
        const el = seeds[key];
        if (el.hasAccess && el.title.length > 0 && el.path !== '/admin') {
                buttons.push(<BasicCard
                    {...el}
                    key={key}
                />);
        }
    }
    return <div style={{width: 800}}>{buttons}</div>;
};

export default Dashboard
