import React, {useEffect} from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import SearchIcon from '@material-ui/icons/Search';
import {useTranslation} from "react-i18next";

const useStyles = makeStyles((theme: Theme): { root: any, searchBar: any } =>
    createStyles({
        root: {
            flexGrow: 1,
            display: "flex"
        },
        searchBar: {
            flexGrow: 1
        },
    })
);

export interface SearchTextInputProps {
    onChange?: any;
    value?: string;
}

const SearchTextInput = ({onChange, value}: SearchTextInputProps) => {
    const classes = useStyles();
    const {t} = useTranslation();

    const [currentState, setCurrentState] = React.useState<any>(value);

    useEffect(() => {
        setCurrentState(value);
    }, [value]);


    const handleChange = ({target: {value}}: any) => {
        setCurrentState(value);
        if (typeof onChange == "function") {
            onChange(value);
        }
    }

    return <div className={classes.root}>
        <TextField
            className={classes.searchBar}
            placeholder={t("Search") + "..."}
            value={currentState}
            onChange={handleChange}
            inputProps={{'aria-label': 'search', 'width': '300'}}
        />
    </div>
}

export default SearchTextInput;