import {
    MenuItem,
    TextField,
} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import {SelectorItemProps} from './StoredSelector';

const prepareSelectorItems = (items: SelectorItemProps[], emptyString: boolean, t: any, onChange: (p: any) => void) => {
    const _items: any[] = [];
    if (emptyString) {
        _items.push(<MenuItem key={(0 - 1)}><em>{t('None')}</em></MenuItem>)
    }
    if (typeof items?.map === "function") {
        items.map(({id, name}: any) => _items.push(<MenuItem onClick={() => onChange(id)} value={id}
                                                             key={id}>{name}</MenuItem>))
    }

    return _items;
}

export interface PlainDropdownSelectProps {
    field: any,
    label: string,
    meta: any,
    items: SelectorItemProps[],
    emptyString: boolean,
    onChange?: any
    style?: any
}

const PlainDropdownSelect = ({ field, label, meta, items, emptyString, onChange, style = {} }: PlainDropdownSelectProps) => {
    const { t } = useTranslation();
    return <TextField
        {...field}
        label={label}
        fullWidth
        select
        helperText={meta.touched && meta.error}
        error={meta.touched && Boolean(meta.error)}
        style={style}
    >
        {prepareSelectorItems(items, emptyString, t, onChange)}
    </TextField>
}

export default PlainDropdownSelect