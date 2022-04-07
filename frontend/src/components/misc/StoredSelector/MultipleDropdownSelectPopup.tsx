import * as React from 'react';
import {
    Dialog,
    DialogTitle,
    MenuItem,
    ListItemText,
    Checkbox,
    Button,
    Toolbar
} from "@material-ui/core";
import { useTranslation } from "react-i18next";


export interface MultipleDropdownSelectPopupProps {
    isOpen?: boolean,
    onClose: () => {},
    items?: any[],
    checked?: any[],
    title?: string,
    onChange?: (checked: any[]) => {}
}
const MultipleDropdownSelectPopup = ({
    isOpen = false,
    onClose,
    onChange,
    items = [],
    checked = [],
    title = "Chooce"
}: MultipleDropdownSelectPopupProps) => {
    const { t } = useTranslation();
    const [_checked, set_checked] = React.useState<any[]>(checked || []);
    const [_isOpen, set_isOpen] = React.useState<boolean>(isOpen || false);

    const onSelectionChange = (id: string) => {
        const currentSelection: any[] = [..._checked];
        const itemIndex = currentSelection.indexOf(id)
        if (itemIndex > -1) {
            currentSelection.splice(itemIndex, 1);
        } else {
            currentSelection.push(id)
        }
        set_checked(currentSelection);
    }
    const _onConfirm = () => {
        onChange ? onChange(_checked) : void (0);
        _onClose();
    }

    const _onClose = () => {
        onClose();
        set_isOpen(false);
    }

    React.useEffect(() => {
        set_isOpen(isOpen || false);
    }, [isOpen]);

    return (<Dialog
        open={_isOpen}
        maxWidth={false}
        onClose={onClose}
    >
        <DialogTitle>{title}</DialogTitle>
        <div style={{ padding: "15px", width: 400, overflow: 'auto' }}>
            {items?.map(({ name, id }) => (
                <MenuItem key={name} value={id} onClick={() => onSelectionChange(id)}>
                    <Checkbox
                        checked={_checked.indexOf(id) > -1} />
                    <ListItemText primary={name} />
                </MenuItem>
            ))}
        </div>
        <Toolbar>
            <div style={{flex:1}}></div>
            <Button
                variant="outlined"
                onClick={_onConfirm}
            >{t('Confirm')}</Button>
            <Button
                variant="outlined"
                onClick={_onClose}
            >{t('Cancel')}</Button>
        </Toolbar>
    </Dialog >)
}

export default MultipleDropdownSelectPopup;
