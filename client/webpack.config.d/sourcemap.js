config.module.rules.push({
    test: /\.js$/,
    loader:require.resolve('source-map-loader'),
    enforce: 'pre'
    }

);