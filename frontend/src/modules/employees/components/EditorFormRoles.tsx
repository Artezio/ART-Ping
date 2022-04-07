import React from "react";
import Divider from "@material-ui/core/Divider";
import {Field, useFormikContext} from "formik";
import {CheckboxWithLabel} from "formik-material-ui";
import {useTranslation} from "react-i18next";
import StoredSelector from "../../../components/misc/StoredSelector/StoredSelector";
import {remove} from "ramda";
import {connect} from "react-redux";
import {AppRootState} from "../../../store";
import {hasAccessEditAdminRoleSelector} from "../../employeesCheck/selectors";

enum ROLES {
    USER = "USER",
    PM = "PM",
    HR = "HR",
    ADMIN = "ADMIN",
    DIRECTOR = "DIRECTOR",
    ROLE_ANONYMOUS = "ROLE_ANONYMOUS"

}

const EditorFormRoles = (props: any) => {
    const {t} = useTranslation();
    const {values, setFieldValue} = useFormikContext<any>();

    const rolesFieldName = "roles";

    const roleChangeHandler = (clickEvent: any, isChecked?: boolean) => {
        let currentRoles: string[] = values[rolesFieldName] ?? [];
        const clickedRoleValue = clickEvent.target.value;
        if (isChecked) {
            currentRoles.push(clickedRoleValue);
        } else {
            currentRoles = remove(currentRoles.indexOf(clickedRoleValue), 1, currentRoles);
        }
        return setFieldValue(rolesFieldName, currentRoles);
    }
    const handleProjectsListChange = (newList: [], prevList: [], fieldChanged: string) => {

        if (fieldChanged === 'projects' && newList.length < prevList.length) {
            prevList.forEach((id) => {
                if (newList.indexOf(id) < 0) {
                    const arrId = values.managedProjects.indexOf(id);
                    values.managedProjects.splice(arrId, 1);
                }
            })
        }
        if (fieldChanged === 'managedProjects' && newList.length < prevList.length) {
            prevList.forEach((id) => {
                if (newList.indexOf(id) < 0) {
                    const arrId = values.projects.indexOf(id);
                    values.projects.splice(arrId, 1);
                }
            })
        }
        if (fieldChanged === 'managedProjects' && newList.length > prevList.length) {
            newList.forEach((id) => {
                if (prevList.indexOf(id) < 0) {
                    values.projects.push(id)
                }
            })
        }
    }
    const fieldAdditionStyle = {marginBottom: "25px"}

    return <div>
        {/**
         * EMPLOYEE (USER)
         */}
        <Field
            type="checkbox"
            component={CheckboxWithLabel}
            name={rolesFieldName}
            value={ROLES.USER}
            label={{label: t("Employee")}}
            onChange={roleChangeHandler}
        />{t("Employee")}
        {values[rolesFieldName]?.indexOf(ROLES.USER) > -1 && <StoredSelector

            style={fieldAdditionStyle}
            store="projects"
            name={"projects"}
            //emptyString
            onAfterChangeValue={(value:any)=>{handleProjectsListChange(value, values.projects, 'projects')}}
            autocomplete
            multiple
            label={t('Projects')}
        />}
        <Divider/>

        {/**
         * DIRECTOR
         */}
        <Field
            type="checkbox"
            component={CheckboxWithLabel}
            name={rolesFieldName}
            value={ROLES.DIRECTOR}
            label={{label: t("Director")}}
            onChange={roleChangeHandler}
        />{t("Director")}
        {values[rolesFieldName]?.indexOf(ROLES.DIRECTOR) > -1 && <StoredSelector
            style={fieldAdditionStyle}
            store="offices"
            name="managedOffices"
            emptyString
            multiple
            autocomplete
            label={t('Offices')}
        />}

        <Divider/>

        {/**
         * PM
         */}
        <Field
            type="checkbox"
            component={CheckboxWithLabel}
            name={rolesFieldName}
            value={ROLES.PM}
            label={{label: t("Project manager")}}
            onChange={roleChangeHandler}
        />{t("Project manager")}
        {values[rolesFieldName]?.indexOf(ROLES.PM) > -1 && <StoredSelector
            style={fieldAdditionStyle}
            store="projects"
            name="managedProjects"
            emptyString
            autocomplete
            multiple
            onAfterChangeValue={(value:any)=>{handleProjectsListChange(value, values.managedProjects, 'managedProjects')}}
            label={t('Projects')}
        />}
        <Divider/>
        {/**
         * HR
         */}
        <Field
            type="checkbox"
            component={CheckboxWithLabel}
            name={rolesFieldName}
            value={ROLES.HR}
            label={{label: t("HR")}}
            onChange={roleChangeHandler}
        />{t("HR")}
        {values[rolesFieldName]?.indexOf(ROLES.HR) > -1 && <StoredSelector
            style={fieldAdditionStyle}
            store="offices"
            name="offices"
            emptyString
            autocomplete
            multiple
            label={t('Offices')}
        />}
        <Divider/>

        {/**
         * ADMIN
         */}
        <Field
            type="checkbox"
            disabled={!props.hasAccessEditAdminRole}
            component={CheckboxWithLabel}
            name={rolesFieldName}
            value={ROLES.ADMIN}
            label={{label: t("Admin")}}
            onChange={roleChangeHandler}
        />{t("Admin")}
        <Divider/>
    </div>
}

const mapStateToProps = (state: AppRootState) => ({
    hasAccessEditAdminRole: hasAccessEditAdminRoleSelector(state),
})

const connector = connect(mapStateToProps)

export default connector(EditorFormRoles)
