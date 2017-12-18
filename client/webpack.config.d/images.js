
config.module.rules.push({
    test: [/\.png$/, /\.jpg$/, /\.gif/],
    loader: require.resolve("url-loader"),
    options : {
        limit: 10000,
        name: 'static/media/[name].[hash:8].[ext]'
    }
});