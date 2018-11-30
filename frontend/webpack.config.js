const path = require('path');

module.exports = {
    entry: './src/index.js',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'public'),
        publicPath: "/"
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: ['babel-loader']
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx'],
        alias: {
            ['~']: path.resolve(__dirname + '/src')
        }
    },
    devServer: {
        contentBase: path.resolve(__dirname, 'public'),
        historyApiFallback: true
    }
};