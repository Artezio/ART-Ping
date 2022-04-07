import React from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import {TypedSelector} from '../../../store';
import Select, {SelectProps} from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';

const useStyles = makeStyles((theme: Theme): { picker: any } =>
    createStyles({
        picker: {
            margin: theme.spacing(1),
            minWidth: 120,
            width: 180,
        }
    }),
);

export interface ComboboxProps extends SelectProps {
    onChange?: any;
    refName?: string;
    defaultValue?: any;
    value?: any;
    displayField?: string;
    valueField: string;
    listTitle: string;
    values?: [];
    okLabel?:any;
    cancelLabel?:any;
}

export default (props: ComboboxProps) => {
    const classes = useStyles();

    const [currentValue, setCurrentValue] = React.useState(props.value || props.defaultValue || "");

    const ref = props.values ? props.values : TypedSelector((state: any) => {
        if (props.refName && state?.references?.refs.hasOwnProperty(props.refName)) {
            return state.references.refs[props.refName];
        } else {
            return {}
        }
    });

    const options: any = [];

    if (props.listTitle) {
        options.push(<MenuItem
            value=""
            disabled
            key={(props.valueField || 'id') + "____"}
        >
            {props.listTitle}
        </MenuItem>)
    }

    if (ref?.list) {
        for (let i in ref.list) {
            const item = ref.list[i];
            options.push(<MenuItem
                value={item[props.valueField || 'id']}
                key={item[props.valueField || 'id']}
            >
                {item[props.displayField || 'name']}
            </MenuItem>)
        }
    }

    const handlePickerChange = (value: any) => {
        setCurrentValue(value?.target?.value || "");
        if (typeof props.onChange == "function") {
            props.onChange(value);
        }
    }

    const picker = <Select
        className={classes.picker}
        displayEmpty
        value={currentValue}
        onChange={handlePickerChange}
    >
        {options}
    </Select>;
    return picker;
}