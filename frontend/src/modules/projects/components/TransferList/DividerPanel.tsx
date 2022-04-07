import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import * as helpers from './helpers';

export interface DividerPanelProps {
    left: any[];
    setLeft: any;
    leftChecked: any[];
    right: any[]
    setRight: any;
    rightChecked: any[];
    checked?:any[];
    setChecked:any;
    hasAccessEditProjects?: boolean;
    setSelectionModel: any;
}

const DividerPanel = ({left,setLeft,leftChecked,right,setRight,rightChecked,checked=[],setChecked, setSelectionModel}: DividerPanelProps) => {

    const handleCheckedRight = () => {
        setRight(right.concat(helpers.not(leftChecked, right)));
        setLeft(helpers.not(left, leftChecked));
        setChecked(helpers.not(checked, leftChecked));
        setSelectionModel([]);
    };

    const handleCheckedLeft = () => {
        setLeft(left.concat(rightChecked));
        setRight(helpers.not(right, rightChecked));
        setChecked(helpers.not(checked, rightChecked));
        setSelectionModel([]);
    };

    return <Grid
        container direction="column">
        {
            //@ts-ignore
            <Button
                sx={{ my: 0.5 }}
                variant="outlined"
                size="small"
                onClick={handleCheckedRight}
                disabled={leftChecked.length === 0 || rightChecked.length > 0}
                aria-label="move selected right"
            >
                &gt;
            </Button>
        }
        {
            //@ts-ignore
            <Button
                sx={{ my: 0.5 }}
                variant="outlined"
                size="small"
                onClick={handleCheckedLeft}
                disabled={rightChecked.length === 0}
                aria-label="move selected left"
            >
                &lt;
            </Button>
        }
    </Grid>
}

export default DividerPanel;
