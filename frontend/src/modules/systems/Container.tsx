import * as React from 'react';
import {useEffect} from 'react';
import {connect, ConnectedProps, useDispatch} from 'react-redux';
import {useTranslation} from "react-i18next";
import {DataGrid} from "@material-ui/data-grid";
import {Backdrop, Button, CircularProgress, DialogActions, DialogContent, TextField} from "@material-ui/core";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {isSystemsLoading, getSystems} from "../../store/system/selectors";
import {AppRootState} from '../../store';
import * as SystemActions from '../../store/system/actions';
import {buttonStyle} from '../../common/styles';
import {FormikProvider, useFormik} from "formik";
import {ValidationSchema} from "./ValidationSchema";

interface Props extends PropsFromRedux {
    // getSystems: any;
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
        toolbar: any,
        dataGrid: any,
        actionColumn: any,
        actionButton: any,
        dialogEdit: any,
        dialogConfirm: any,
        backdrop: any
    } =>
        createStyles({
            root: {
                height: "calc(100% - 150px)",
                width: '100%',
            },
            toolbar: {},
            dataGrid: {
                backgroundColor: "#FFFFFF",
            },
            actionColumn: {
                backgroundColor: "#000000",
            },
            actionButton: {},
            dialogEdit: {},
            dialogConfirm: {},
            backdrop: {
                zIndex: theme.zIndex.drawer + 1,
                color: '#fff',
            },
        })
);

const PageSystem = ({
                        system = [],
                        isSystemsLoading = false,
                        getSystems,
                    }: Props) => {
    const classes = useStyles();
    const {t} = useTranslation();

    useEffect(() => {
        if (getSystems)
            getSystems();
    }, [getSystems]);

    const initialSettings: any = {};
    const availibleSettings = system.filter((setting: any) => {
        if (setting.isCustomizable) {
            initialSettings[setting.name] = setting.value;
            return true;
        }
    });
    const formikbag = useFormik({
        enableReinitialize: true,
        initialValues: {...initialSettings},
        validationSchema: ValidationSchema,
        onSubmit: (values: any) => {
            dispatch(SystemActions.saveSettings(values));
        },
    });

    const {values, setFieldValue, handleSubmit} = formikbag;

    const columns = [
        {
            field: 'title',
            flex: 1,
            headerName: t('Property name'),
            sortable: false,
            disableClickEventBubbling: true,
            disableColumnMenu: true,
            renderCell: ({row: {title}}: any) => {
                return t(title);
            }
        },
        {
            field: 'value',
            flex: 1,
            headerName: t('Property value'),
            sortable: false,
            disableClickEventBubbling: true,
            disableColumnMenu: true,
            renderCell: (params: any) => {
                return (
                    <TextField
                        value={values[params.row.name]}
                        onChange={(e) => {
                            setFieldValue(params.row.name, e.target.value)
                        }
                        }
                        error={Boolean(formikbag.touched[params.row.name]) && Boolean(formikbag.errors[params.row.name])}
                        helperText={formikbag.errors[params.row.name]}
                    />

                )
            }
        },
    ];
    const dispatch = useDispatch();

    const saveSettings = () => {
        handleSubmit();
    };

    return <div className={classes.root}>
        <FormikProvider value={formikbag}>
            <DialogContent>
                <DataGrid
                    className={classes.dataGrid}
                    rows={availibleSettings || []}
                    columns={columns}
                    pageSize={20}
                    checkboxSelection={false}
                    autoHeight={true}
                    hideFooter={true}/>
            </DialogContent>

            <DialogActions>
                <Button style={buttonStyle}
                        onClick={saveSettings}
                        color="secondary" variant="contained"
                    // disabled={!isValid}
                >
                    {t('Save')}
                </Button>
            </DialogActions>
        </FormikProvider>
        <Backdrop
            className={classes.backdrop}
            open={isSystemsLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </div>
}

const mapStateToProps = (state: AppRootState) => {
    return ({
        system: getSystems(state),
        isSystemsLoading: isSystemsLoading(state),
    });
}

const mapDispatchToProps = (dispatch: any) => {
    return {
        getSystems: () => {
            dispatch(SystemActions.getList());
        },
    };
};

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

export default connector(PageSystem);
