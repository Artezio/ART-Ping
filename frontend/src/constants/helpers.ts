export function mergeEnvAndPreset(prefix: string = "", preset: any) {
    const config = Object.assign({}, preset);
    const configKeys = Object.keys(preset);
    for (let i in configKeys) {
        const paramKey = `${prefix}${configKeys[i].toUpperCase()}`;
        if (process.env[paramKey]) {
            config[configKeys[i]] = process.env[paramKey]
        }
    }

    return config;
}