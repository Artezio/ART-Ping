import {Field} from "formik";
import {TypedSelector} from '../../../store';
import PlainDropdownSelect from './PlainDropdownSelect';
import MultipleSelect from "./MultipleSelect";
import {ADMIN} from "../../../store/user/constants";

export interface SelectorItemProps {
    id: string,
    name: string
}

export interface StoredSelectorProps {
    store: string | SelectorItemProps[],
    name: string,
    label?: string,
    emptyString?: boolean,
    autocomplete?: boolean,
    multiple?: boolean,
    onChange?: (param: any) => void,
    style?: any,
    currentUser?: any,
    onAfterChangeValue?: (value: any) => void,
}

const StoredSelector = ({
                            store,
                            name,
                            label = "",
                            emptyString = false,
                            autocomplete = false,
                            multiple = false,
                            onChange = (a: any) => {
                            },
                            style = {},
                            onAfterChangeValue,
                            ...rest
                        }: StoredSelectorProps) => {
    const items = (typeof store === 'string') ? TypedSelector((state: any) => {
        // if (state?.references?.refs.hasOwnProperty(store) && store==='offices' && state?.user.roles.indexOf(ADMIN) === -1) {
        //     return state.references.refs[store].list.filter((item: any) => item.id === state?.user.baseOffice)
        // }
        if (state?.references?.refs.hasOwnProperty(store)) {
            return state.references.refs[store].list;
        } else {
            return {}
        }
    }) : store;
    return <Field
        id={`StoredSelector_${name}`}
        name={name}
        render={({field, meta}: any) => {
            let input = <></>
            if (multiple) {
                input = <MultipleSelect {...{
                    field,
                    label,
                    meta,
                    items,
                    emptyString,
                    autocomplete,
                    style,
                    onAfterChangeValue,
                    ...rest
                }}/>;
            } else {
                input = <PlainDropdownSelect {...{
                    field,
                    label,
                    meta,
                    items,
                    emptyString,
                    autocomplete,
                    onChange,
                    style,
                    ...rest
                }}/>;
            }
            return input;
        }}
    />
}

export default StoredSelector;
