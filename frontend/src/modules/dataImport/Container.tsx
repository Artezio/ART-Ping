import * as React from 'react';
import {useTranslation} from "react-i18next";
import {Backdrop, Button, CircularProgress} from "@material-ui/core";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";
import {buttonStyle} from '../../common/styles';
import {FieldArray, Form, Formik} from "formik";
import {ValidationSchema} from "./ValidationSchema";
import {importDataAction} from "../../store/system/actions";
import FileOpenIcon from "@material-ui/icons/Search";
import { useDispatch } from 'react-redux';
import * as ReferencesActions from "../../store/references/actions";


const useStyles = makeStyles((theme: Theme): {
        root: any,
        backdrop: any,
        inputWrapper: any,
        inputFile: any,
        error: any,
        selectFileButton: any,
    } =>
        createStyles({
            root: {
                height: "calc(100% - 150px)",
                width: '100%',
            },
            backdrop: {
                zIndex: theme.zIndex.drawer + 1,
                color: '#fff',
            },
            selectFileButton: {
                color: "#000",
                padding: "0",
                '& label': {
                    display: "flex",
                    cursor: "pointer",
                    justifyContent: "center",
                    alignItems: "center",
                    height: "35px",
                    width: "200px",
                },

            },
            inputWrapper: {
                display: "flex",
                flexDirection: "row",
                alignItems: "center",
                marginTop: "30px",
            },
            inputFile: {
                opacity: "0",
                visibility: "hidden",
                position: "absolute",
            },
            error: {
                margin: "5px",
                color: "red",
                height: "30px",
            },
        })
);

const DataImport = () => {
    const classes = useStyles();
    const dispatch = useDispatch();
    const {t} = useTranslation();
    const [requestState, setRequestState] = React.useState({
        sendingRequest: false,
    });
    const [selectedFileName, setSelectedFileName] = React.useState({
        fileName: "",
    });
    const getError = (touched: boolean, error: any) => {
        return touched && error && <p key={error} className={'error'}>{error}</p>
    }
    const getFileSchema = (file: any) => (file && {
        file: file,
        type: file.type,
        name: file.name
    })
    const getArrErrorsMessages = (errors: any) => {
        const result: any[] = []
        errors && Array.isArray(errors) && errors.forEach((value) => {
            if (typeof value === 'string') {
                result.push(value)
            } else {
                Object.values(value).forEach((error) => {
                    result.push(error)
                })
            }
        })
        return result;
    }
    const changeSelectedFileName = (name: string) => {
        setSelectedFileName({fileName: name})
    }
    const handleSubmit = async (files: any) => {
        setRequestState({
            sendingRequest: true,
        })
        await importDataAction(files.file[0].file);
        dispatch(ReferencesActions.GetReferencesList());
        setRequestState({
            sendingRequest: false,
        })
    }

    return <div className={classes.root}>
        <Formik
            initialValues={{
                file: null,
            }}
            onSubmit={e => handleSubmit(e)}
            validationSchema={ValidationSchema}
        >
            {(props: any) => (
                <Form
                    onSubmit={props.handleSubmit}
                >
                    <FieldArray name={`file`}>
                        {(arrayHelper) => (
                            <>
                                <div className={classes.inputWrapper}>
                                    <Button style={buttonStyle}
                                            className={classes.selectFileButton}
                                            color="secondary"
                                            variant="outlined"
                                    >
                                        <label htmlFor="file">
                                            <input
                                                id={`file`}
                                                type={`file`}
                                                name={`file`}
                                                className={classes.inputFile}
                                                onChange={(event) => {
                                                    const {files} = event.target
                                                    const file = getFileSchema(files?.item(0))
                                                    if (!file) {
                                                        arrayHelper.remove(0)
                                                        changeSelectedFileName("");
                                                    } else {
                                                        changeSelectedFileName(files ? files[0].name : "");
                                                        if (Array.isArray(props.values.file)) {
                                                            arrayHelper.replace(0, file)

                                                        } else {
                                                            arrayHelper.push(file)
                                                        }
                                                    }
                                                }}
                                            />
                                            <FileOpenIcon/>
                                            {t('Choose file')}
                                        </label>
                                    </Button>
                                    <div style={{marginLeft: "10px"}}>
                                        {selectedFileName.fileName ? `${t('Selected file')}: ${selectedFileName.fileName}` : t('No file selected')}
                                    </div>
                                </div>
                                <div className={classes.error}>
                                    {props.errors ? getArrErrorsMessages(props.errors.file).map((error) => getError(true, error)) : ""}
                                </div>
                            </>
                        )}
                    </FieldArray>
                    <Button style={buttonStyle}
                            type="submit"
                            color="secondary"
                            variant="contained"
                    >
                        {t('Import data')}
                    </Button>
                </Form>
            )}
        </Formik>
        <Backdrop
            className={classes.backdrop}
            open={requestState.sendingRequest}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </div>
}

export default DataImport;
