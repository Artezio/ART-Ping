import React, {useMemo} from 'react';

import {useHistory, useLocation} from "react-router-dom";

import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import MenuItem from '@material-ui/core/MenuItem';
import MenuList from '@material-ui/core/MenuList';

import {getAdminItemSeeds} from '../AdminMenuItemsSeeds';

import Paper from '@material-ui/core/Paper';
import {useTranslation} from "react-i18next";
import {useSelector} from "react-redux";
import {
    hasAccessToPageEmployeesSelector,
    hasAccessToPageOfficesSelector,
    hasAccessToPageCalendarsSelector,
    hasAccessToPageProjectsSelector,
    hasAccessToPageSystemSettingsSelector,
    hasAccessToPageDataImportSelector,
} from "../../../../modules/employeesCheck/selectors";

const useStyles = makeStyles((theme: Theme): { paper: any } =>
    createStyles({
        paper: {
            padding: theme.spacing(0),
            textAlign: 'center',
            color: theme.palette.text.secondary,
        }
    }),
)


const AdminMenu = (props: any) => {
    const classes = useStyles();
    const {t} = useTranslation();
    const location = useLocation();
    const history = useHistory();
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

        if (el.title.length > 0 && el.hasAccess) {

            buttons.push(<MenuItem
                key={key}
                selected={location.pathname === el.path}
                onClick={() => {
                    history.push(el.path);
                }}
            >
                {t(el.title)}
            </MenuItem>);
        }
    }

    return <Paper className={classes.paper}>
        <MenuList>
            {buttons}
        </MenuList>
    </Paper>
}

export default AdminMenu;
