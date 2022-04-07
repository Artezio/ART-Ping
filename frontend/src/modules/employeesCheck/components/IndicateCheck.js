import {green, grey, red, blue} from "@material-ui/core/colors";
import CachedIcon from '@mui/icons-material/Cached';
import DesktopWindowsIcon from '@mui/icons-material/DesktopWindows';
import DesktopAccessDisabledIcon from '@mui/icons-material/DesktopAccessDisabled';
import CancelIcon from '@mui/icons-material/Cancel';
import TimerIcon from '@mui/icons-material/Timer';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';


export const InProcessIcon = () => {
    return <CachedIcon style={{color: grey[500], fontSize: 15}}/>
};
export const DesktopWindowIcon = () => {
    return <DesktopWindowsIcon style={{color: green[500], fontSize: 15}}/>
};
export const DesktopAccessDisableIcon = () => {
    return <DesktopAccessDisabledIcon style={{color: grey[900], fontSize: 15}}/>
};
export const ValidCoords = () => {
    return <CheckCircleIcon style={{color: blue[800], fontSize: 10, position: 'absolute', left: "8px", bottom: "8px"}}/>
};
export const NoAnswerIcon = () => {
    return <CancelIcon style={{color: red[500], fontSize: 15}}/>
};
export const CheckedIconTrue = () => {
    return <AccountCircleIcon style={{color: green[500], fontSize: 30}}/>
};

export const CheckedIconFalse = () => {
    return <AccountCircleIcon style={{color: red[500], fontSize: 30}}/>
};

export const PlannedIcon = () => {
    return <TimerIcon style={{color: grey[500], fontSize: 15}}/>
};
