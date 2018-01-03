'use strict';
const path = require('path');
var webpack = require('webpack');

var config = {
    "context": path.resolve(".", "classes/kotlin/test"),
    "entry": {
        "main": "./client_test"
    },
    "output": {
        "path": "testbundle",
        "filename": "[name].bundle.js",
        "chunkFilename": "[id].bundle.js",
        "publicPath": "/"
    },
    "module": {
        "rules": [

        ]
    },
    "resolve": {
        "modules": [
            "classes/kotlin/test/dependencies",
            "classes/kotlin/main",
            "resources/main",
            "resources/test",
            "build/node_modules"
        ]
    },
    "plugins": [

    ]
};
var defined = {
    "PRODUCTION": false
};
config.plugins.push(new webpack.DefinePlugin(defined));

module.exports = config;

// from file /mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/webpack.config.d/css.js
const ExtractTextPlugin = require('extract-text-webpack-plugin');

config.module.rules.push({
    test: /\.css$/,
    loader: ExtractTextPlugin.extract(
        Object.assign(
            {
                fallback: require.resolve('style-loader'),
                use: [
                    {
                        loader: require.resolve('css-loader'),
                        options: {
                            importLoaders: 1,
                            minimize: true,
                            sourceMap: true,
                        },
                    }]
            },
            {}
        )
    ),
});

config.plugins.push(
    new ExtractTextPlugin({
        filename: '[name].css',
    }))
/*
config.module.rules.push({
    test: /\.css$/,
    use: [
        require.resolve('style-loader'),
        {
            loader: require.resolve('css-loader'),
            options: {
                importLoaders: 1,
            },
        },
        ]
});
*/
// from file /mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/webpack.config.d/images.js

config.module.rules.push({
    test: [/\.png$/, /\.jpg$/, /\.gif/],
    loader: require.resolve("url-loader"),
    options : {
        limit: 10000,
        name: 'static/media/[name].[hash:8].[ext]'
    }
});

// from file /mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/webpack.config.d/resources.js
config.resolve.modules.push("src/main/kotlin");

// from file /mnt/c/Users/synopia/IdeaProjects/legion2-builder/client/webpack.config.d/sourcemap.js
config.module.rules.push({
        test: /\.js$/,
        loader:require.resolve('source-map-loader'),
        enforce: 'pre'
    }

);
