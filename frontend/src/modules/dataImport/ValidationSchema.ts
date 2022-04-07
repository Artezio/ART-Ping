import * as yup from "yup";
export const ValidationSchema = yup.object().shape({
    file:
        yup.array().of(yup.object().shape({
        file: yup.mixed(),
        type: yup.string().oneOf([
            'application/vnd.ms-excel',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            'application/msexcel',
            'application/x-msexcel',
            'application/x-ms-excel',
            'application/x-excel',
            'application/x-dos_ms_excel',
            'application/xls',
            'application/x-xls'], 'Неверный формат файла (поддерживаются xls, xlsx)').required(),
        name: yup.string().required()
    }).typeError('Добавьте файл')).required(),
});


