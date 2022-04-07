import React, {useMemo} from 'react';
import {Redirect, Route, Switch} from "react-router-dom";
import 'react-toastify/dist/ReactToastify.css';
import {default as EmployeeList} from '../../modules/employeesCheck/Container';
import {NotificationConnect} from '../../modules/NotificationConnect'
import TopBar from './TopBar/TopBar';
import UserPingPanel from './UserPingPanel/UserPingPanel';
import AdminPanel from './AdminPanel/AdminPanel';
import MainAuthForm from "../AuthForm/MainAuthForm/MainAuthForm";
import {TypedSelector} from "../../store";
import {ADMIN} from "../../store/user/constants";

const MainContent = () => {
    const currentUser = TypedSelector(state => state.user);
    const isAdminUser = useMemo(() => {
        return currentUser.roles.indexOf(ADMIN) !== -1
    },[currentUser.roles]);

    return (
        <div style={{ width: "100%", height: "100%", overflow: "hidden" }}>
            <NotificationConnect>
                <TopBar />
                <Switch>
                    {/*<Route exact path="/">*/}
                    {/*    <UserPingPanel />*/}
                    {/*</Route>*/}
                    <Route path="/employee-checks">
                        <EmployeeList />
                    </Route>
                    <Route path="/admin">
                        <AdminPanel />
                    </Route>

                    <Route exact path="/">
                        {isAdminUser ? <Redirect to="/admin" /> : <UserPingPanel />}
                    </Route>
                </Switch>
            </NotificationConnect>
        </div>
    );
}

export default MainContent;
