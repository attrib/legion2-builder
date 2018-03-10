config.mode="development";
if( defined.PRODUCTION ) {
    config.mode="production";
    const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
    config.optimization = {};
    config.optimization.minimizer = [new UglifyJSPlugin({
        uglifyOptions: {
            compress: {
                warnings: false,
                // Disabled because of an issue with Uglify breaking seemingly valid code:
                // https://github.com/facebookincubator/create-react-app/issues/2376
                // Pending further investigation:
                // https://github.com/mishoo/UglifyJS2/issues/2011
                comparisons: false
            },
            output: {
                comments: false
            },
            sourceMap: true
        }
    })];
}