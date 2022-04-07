import * as React from 'react';
import {useFormikContext} from "formik";
import {Checkbox, ListItemText, MenuItem, Select} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import {SelectorItemProps} from './StoredSelector';

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

interface MultipleSelectProps {
  field: any,
  label: string,
  meta: any,
  items: SelectorItemProps[],
  emptyString: boolean,
  autocomplete: boolean,
  style?: any,
}

const MultipleSelect = ({
                          field,
                          label,
                          meta,
                          items,
                          emptyString,
                          style = {}
                        }: MultipleSelectProps) => {
  const {t} = useTranslation();

  const {setFieldValue} = useFormikContext<any>();

  const handleChange = (value: string[]) => {
    setFieldValue(field.name, value);
  };
  return (
      <Select
          {...field}
          label={label}
          fullWidth
          multiple
          onChange={handleChange}
          renderValue={(selected: any) => selected.join(', ')}
          MenuProps={MenuProps}
          helperText={meta.touched && meta.error}
          error={meta.touched && Boolean(meta.error)}
          style={style}
      >
        {items.map(({id, name}) => (
            <MenuItem key={id} value={id}>
              <Checkbox checked={field.value.indexOf(id) > -1}/>
              <ListItemText primary={name}/>
            </MenuItem>
        ))}
      </Select>
  )
}

export default MultipleSelect;