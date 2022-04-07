const isEqual = (object1: any , object2: any) => {
    const props1 = Object.getOwnPropertyNames(object1);
    const props2 = Object.getOwnPropertyNames(object2);
    if (props1.length !== props2.length) {
        return false;
    }
    for (let i = 0; i < props1.length; i += 1) {
        const prop = props1[i];
        const bothAreObjects = typeof(object1[prop]) === 'object' && typeof(object2[prop]) === 'object';

        if ((!bothAreObjects && (object1[prop] !== object2[prop]))
            || (bothAreObjects && !isEqual(object1[prop], object2[prop]))) {
            return false;
        }
    }
    return true;
}

export const not = (a: readonly any[], b: readonly any[]) => {
    return a.filter((valueA) => {
        let result = true;
        b.forEach(valueB => {
            if (isEqual(valueB, valueA)) {
                result = false;
            }
        })
        return result;
    });
}

export const union = (a: readonly any[], b: readonly any[]) => {
    return [...a, ...not(b, a)];
}

export const intersection = (a: readonly any[], b: readonly any[]) => {
    return a.filter((valueA) => {
        let result = false;
        b.forEach(valueB => {
            if (isEqual(valueB, valueA)) {
                result = true;
            }
        })
        return result;
    });
}
