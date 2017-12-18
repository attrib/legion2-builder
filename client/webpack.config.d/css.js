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