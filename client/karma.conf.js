var webpackConfig = require("webpack.karma.config.js");
webpackConfig.resolve.modules.push("classes/kotlin/test/client_test.js");

module.exports = function (config) {
    config.set({
        "basePath": ".",
        "frameworks": [
            "qunit",
            "es6-shim"
        ],
        "reporters": [
            "progress",
            "junit"
        ],
        "files": [
            "classes/kotlin/test/client_test.js"
        ],
        "exclude": [
            "*~",
            "*.swp",
            "*.swo"
        ],
        "port": 9876,
        "runnerPort": 9100,
        "colors": false,
        "autoWatch": true,
        "browsers": [
            "PhantomJS"
        ],
        "captureTimeout": 60000,
        "singleRun": false,
        "preprocessors": {
            "classes/kotlin/test/client_test.js": [
                "sourcemap",
                "webpack"
            ]
        },
        "plugins": [
            "karma-phantomjs-launcher",
            "karma-es6-shim",
            "karma-junit-reporter",
            "karma-qunit",
            "karma-sourcemap-loader",
            "karma-webpack"
        ],
        "client": {
            "clearContext": false,
            "qunit": {
                "showUI": true,
                "testTimeout": 5000
            }
        },
        "junitReporter": {
            "outputFile": "reports/karma.xml",
            "suite": "karma"
        },
        "webpack": webpackConfig
    })
};