var webpackConfig = require("/mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/build/webpack.karma.config.js");
webpackConfig.resolve.modules.push("/mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/build/classes/kotlin/test/client_test.js");

module.exports = function (config) {
    config.set({
        "basePath": "/mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/build",
        "frameworks": [
            "qunit",
            "es6-shim"
        ],
        "reporters": [
            "progress",
            "junit"
        ],
        "files": [
            "/mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/build/classes/kotlin/test/client_test.js"
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
            "/mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/build/classes/kotlin/test/client_test.js": [
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
            "outputFile": "/mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/build/reports/karma.xml",
            "suite": "karma"
        },
        "webpack": webpackConfig
    })
};