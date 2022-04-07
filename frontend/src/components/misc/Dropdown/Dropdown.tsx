import React from 'react';
import {MenuItem, TextField} from "@material-ui/core";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme): { dropdown: any } =>
    createStyles({
        dropdown: {
            margin: theme.spacing(1),
            minWidth: 120,
            width: 180,
        }
    }),
);


export interface DropdownProps {

    label: string,
    placeholder?: string,
    onChange?: any;
    defaultValue?: any;
    value?: any;
    values: Array<any>;
}

const Dropdown = ({
                      value,
                      label,
                      placeholder = "Choose value",
                      defaultValue,
                      onChange,
                      values = [],
                  }: DropdownProps) => {

    const classes = useStyles();
    const handlePickerChange = ({target: {value}}: any) => {
        if (typeof onChange == "function") {
            onChange(value ? value : null);
        }
    }

    return <TextField
        className={classes.dropdown}
        label={label}
        value={value ? value : ""}
        fullWidth
        select
        onChange={handlePickerChange}
        defaultValue={defaultValue ? defaultValue : ""}

    >
        <MenuItem>
            <em>{placeholder}</em>
        </MenuItem>
        {values.map(({id, name}: any) => <MenuItem
            key={id}
            value={id}>{name}</MenuItem>
        )}
    </TextField>;
}

export default Dropdown;