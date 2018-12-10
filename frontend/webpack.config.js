const path = require('path');

module.exports = {
    entry: './src/main/react/index.js',
    devtool: 'sourcemaps',
    cache: true,
    mode: 'development',
    output: {
        filename: './src/main/resources/static/build/bundle.js',
        path: __dirname
    },
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            }
        ]
    }
};