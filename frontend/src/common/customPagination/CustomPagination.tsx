import * as React from 'react';
import Pagination from "@material-ui/lab/Pagination"
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import {useTranslation} from "react-i18next";
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme): {
        root: any,
        itemsCountSelect: any,
        selectCount: any,
        counter: any,
    } =>
        createStyles({
            root: {
                display: "flex",
                width: "100%",
                justifyContent: "space-between",
                alignItems: "center",
                zIndex: 100
            },
            itemsCountSelect: {
                display: "flex",
                alignItems: "center",
            },
            selectCount: {
                marginLeft: "10px",
                marginRight: "30px",
            },
            counter: {
                marginLeft: "10px",
            }
        })
);
function CustomPagination (props: any) {
    const { total, count, page, itemsOnPage, onPageChange, onItemsOnPageChange } = props;
    const classes = useStyles();
    const counterString = () => {
        const counterFrom = ( itemsOnPage * page ) + 1;
        let counterTo = itemsOnPage * ( page + 1 );
        if (counterTo > total) {
            counterTo = total;
        }
        return `${counterFrom} - ${counterTo} ${t('of')} ${total}`
    }

    const {t} = useTranslation();

    return (
        <div className={classes.root}>
            <div className={classes.counter}>
                {counterString()}
            </div>
            <div className={classes.itemsCountSelect}>
                <label>{t('Items on page')}: </label>
                <Select
                    value={itemsOnPage}
                    className={classes.selectCount}
                    inputProps={{'aria-label': 'Without label'}}
                    onChange={(event) => onItemsOnPageChange(event.target.value)}
                >
                    <MenuItem value={10}>10</MenuItem>
                    <MenuItem value={20}>20</MenuItem>
                    <MenuItem value={50}>50</MenuItem>
                    <MenuItem value={100}>100</MenuItem>
                </Select>

                <Pagination
                    color="primary"
                    count={count}
                    page={page + 1}
                    onChange={(event, value) => onPageChange(value - 1)}
                />
            </div>
        </div>

    );
}

export default CustomPagination;
