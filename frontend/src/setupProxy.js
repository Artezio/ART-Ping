// src/setupProxy.js
//
// @ts-ignore: isolated modules error

const {createProxyMiddleware} = require('http-proxy-middleware');
module.exports = function (app) {
    app.use("/api",
        createProxyMiddleware({
            target: "https://artping-dev-comm.artezio.net",
            changeOrigin: true,
        }));
};