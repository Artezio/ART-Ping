export default function parseJWT(token: string) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url
        .replace(/-/g, '+')
        .replace(/_/g, '/');
    const URIComponent = Buffer
        .from(base64, 'base64')
        .toString()
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('');
    return JSON.parse(decodeURIComponent(URIComponent));
};