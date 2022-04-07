import React, {useMemo} from 'react';
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import {useTranslation} from "react-i18next";

import {getAdminItemSeeds} from './AdminMenuItemsSeeds';
import AdminMenu from './components/AdminMenu';
import {useSelector} from "react-redux";
import {
    hasAccessToPageCalendarsSelector,
    hasAccessToPageDataImportSelector,
    hasAccessToPageEmployeesSelector,
    hasAccessToPageOfficesSelector,
    hasAccessToPageProjectsSelector,
    hasAccessToPageSystemSettingsSelector
} from "../../../modules/employeesCheck/selectors";

const useStyles = makeStyles((theme: Theme): {
        root: any,
        leftBar: any,
        mainBar: any,
        mainGrid: any,
        sectionPaper: any
    } =>
        createStyles({
            root: {
                flexGrow: 1,
                height: 'calc(100% - 90px)',
                "& .MuiButton-containedSecondary": {
                    backgroundColor: "#f44336",
                },
                "& .MuiButton-textSecondary": {
                    color: "#f44336",
                },
                "& .MuiDataGrid-root": {
                    fontSize: "12px"
                },
                "& .MuiDataGrid-columnHeaderTitle": {
                    fontWeight: "bold"
                },
                "& .MuiDataGrid-cell": {
                    wordWrap: "break-word",
                    // display: "inline ",
                    // verticalAlign: "center",
                    whiteSpace: "normal !important",
                    lineHeight: "12px !important",
                    textOverflow: "ellipsis"
                },
            },

            leftBar: {
                paddingRight: "10px !important",
            },

            mainBar: {
                // paddingRight: "10px !important",
            },

            mainGrid: {
                height: "100%",
                padding: "10px !important",
            },

            sectionPaper: {
                height: "100%",
                display: "block",
                padding: theme.spacing(1),
            }
        })
);


const AdminPanel = (props: any) => {
    const classes = useStyles();
    const {t} = useTranslation();
    const hasAccessToPageSystemSettings = useSelector(hasAccessToPageSystemSettingsSelector);
    const hasAccessToPageProjects = useSelector(hasAccessToPageProjectsSelector);
    const hasAccessToPageCalendars = useSelector(hasAccessToPageCalendarsSelector);
    const hasAccessToPageOffices = useSelector(hasAccessToPageOfficesSelector);
    const hasAccessToPageEmployees = useSelector(hasAccessToPageEmployeesSelector);
    const hasAccessToPageDataImport = useSelector(hasAccessToPageDataImportSelector)
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
    const routes = () => {
        const rs = [];

        for (let key in seeds) {

            const el = seeds[key];
            rs.push(<Route path={el.path} exact={el.exact} key={key}>

                <Paper className={classes.sectionPaper}>
                    <Typography variant="h5">{t("Admin panel")}</Typography>
                    <Typography variant="subtitle1" gutterBottom>{t(el.title || "")}</Typography>
                    {el.component}
                </Paper>
            </Route>);
        }
        return rs;
    }

    return (
        <div className={classes.root}>
            <Router>
                <Grid container spacing={2} className={classes.mainGrid}>
                    <Grid className={classes.leftBar} item xs={2}>
                        <AdminMenu/>
                    </Grid>
                    <Grid className={classes.mainBar} item xs={10}>
                        <Switch>{routes()}</Switch>
                    </Grid>
                </Grid>
            </Router>
        </div>);
}

export default AdminPanel;
