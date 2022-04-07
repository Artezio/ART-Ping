import * as React from 'react';
import {useCallback} from 'react';
import {useFormikContext} from "formik";
import {Checkbox, ListItemText, MenuItem, Select, InputLabel, FormControl} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import {SelectorItemProps} from './StoredSelector';
import Divider from "@material-ui/core/Divider";

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 6 + ITEM_PADDING_TOP,
        },
    },
    anchorOrigin: {
        vertical: "bottom",
        horizontal: "left"
    },
    transformOrigin: {
        vertical: "top",
        horizontal: "left"
    },
    getContentAnchorEl: null
};

interface MultipleSelectProps {
    field: any,
    label: string,
    meta: any,
    items: SelectorItemProps[],
    emptyString: boolean,
    autocomplete: boolean,
    style?: any,
    onAfterChangeValue?: (value: any) => void
}


const MultipleSelect = ({
                            field,
                            label,
                            meta,
                            items,
                            emptyString,
                            style = {},
                            onAfterChangeValue
                        }: MultipleSelectProps) => {
    const {t} = useTranslation();

    const {setFieldValue} = useFormikContext<any>();

    const handleChange = ({target: {value}}: any) => {


        setFieldValue(field.name, value);
        if (onAfterChangeValue)
            onAfterChangeValue(value)
    };

    const prepareSelectorText = useCallback((selected: string[]) => {
        const _items: string[] = [];
        if (selected.length < 1) {
            _items.push(emptyString ? t('None') : "")
        }
        if (typeof selected?.map === "function") {
            items.map(({id, name}: SelectorItemProps) => selected.includes(id) ? _items.push(name) : void (0))
        }
        return _items.join(', ');
    }, [items, emptyString]);


    return (
        <div>
            <Divider/>
            <FormControl style={{width: "100%"}}>
                <InputLabel id="multiple-name-label">{label}</InputLabel>
                <Select
                    {...field}
                    labelId="multiple-name-label"
                    fullWidth
                    autoWidth
                    multiple
                    onChange={handleChange}
                    renderValue={(selected: any) => prepareSelectorText(selected)}
                    variant="standard"
                    MenuProps={MenuProps}
                    helperText={meta.touched && meta.error}
                    error={meta.touched && Boolean(meta.error)}
                >
                    {items.map(({id, name}) => (
                        <MenuItem key={id} value={id}>
                            <Checkbox checked={field.value.indexOf(id) > -1}/>
                            <ListItemText primary={name}/>
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>

        </div>
    )
}

export default MultipleSelect;
