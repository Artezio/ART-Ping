import List from '@material-ui/core/List';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Checkbox from '@material-ui/core/Checkbox';
import Divider from '@material-ui/core/Divider';
import { useTranslation } from "react-i18next";
import * as helpers from './helpers';
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";


export interface ListPanelProps {
    title: string;
    items: number[];
    checked?: any;
    setChecked: any;
    setSelectionModel: any;
}

const useStyles = makeStyles((theme: Theme): {
        root: any,
    } =>
        createStyles({
            root: {
                height: "100%",
                "& .MuiCardHeader-root": {
                    padding: "7px"
                },
                "& .MuiTypography-body2": {
                    fontSize: "12px"
                }
            },
        })
);

const ListPanel = ({ title, items, checked = [], setChecked, setSelectionModel }: ListPanelProps) => {
    const { t } = useTranslation();
    const classes = useStyles();
    const numberOfChecked = (items: number[]) =>
        helpers.intersection(checked, items).length;

    const handleToggle = (value: number) => () => {
        setSelectionModel([]);
        const currentIndex = checked.indexOf(value);
        const newChecked = [...checked];
        if (currentIndex === -1) {
            newChecked.push(value);
        } else {
            newChecked.splice(currentIndex, 1);
        }
        setChecked(newChecked);
    };

    const handleToggleAll = (items: number[]) => () => {
        setSelectionModel([]);
        const newChecked = (numberOfChecked(items) === items.length) ? (helpers.not(checked, items)) : (helpers.union(checked, items));
        setChecked(newChecked);
    };
    return (
        <Card
            className={classes.root}
        >
            {
                //@ts-ignore
                <CardHeader
                    sx={{ px: 2, py: 1}}
                    avatar={
                        <Checkbox
                            onClick={handleToggleAll(items)}
                            checked={numberOfChecked(items) === items.length && items.length !== 0}
                            indeterminate={
                                numberOfChecked(items) !== items.length && numberOfChecked(items) !== 0
                            }
                            disabled={items.length === 0}
                            inputProps={{
                                'aria-label': t('all items selected'),
                            }}
                        />
                    }
                    title={title}
                    subheader={`${numberOfChecked(items)}/${items.length} ${t('selected')}`}
                />
            }
            <Divider />
            <List
                dense
                component="div"
                role="list"
                style={{
                    height: "calc(100% - 54px)",
                    overflow: "auto"
                }}
            >
                {items.map((value: any) => {
                    const labelId = `transfer-list-all-item-${value.id}-label`;
                    return (
                        <ListItem
                            key={value.id}
                            role="listitem"
                            button
                            onClick={handleToggle(value)}
                        >
                            <ListItemIcon>
                                <Checkbox
                                    checked={checked.indexOf(value) !== -1}
                                    tabIndex={-1}
                                    disableRipple
                                    inputProps={{
                                        'aria-labelledby': labelId,
                                    }}
                                />
                            </ListItemIcon>
                            <ListItemText id={labelId} primary={`${value.name}`} />
                        </ListItem>
                    );
                })}
                <ListItem />
            </List>
        </Card>
    )
};

export default ListPanel;
