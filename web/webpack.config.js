var path = require('path');
var webpack = require('webpack');
module.exports = {
    entry: {
        index: './src/javascripts/index.js'
    },
    output: {
        path: __dirname + '/../app/src/main/assets/javascripts',
        publicPath: '/assets/',
        filename: 'bundle.js'
    },
        node: {
            /* even though the 'fs' module is never used here, some libs have dependency on it
             * so it needs to be stubbed  */
            fs: 'empty'
        },
    module: {
           loaders: [
               {
                   test: /\.js?$/,
                   exclude: /node_modules/,
                   loader: "babel-loader",
                   query: {
                       presets: ['react', 'es2015', 'stage-0'],
                       plugins: ['transform-runtime',"transform-decorators-legacy", "transform-class-properties"]
                   }
               },
               {
                   test: /\.css$/,
                   loader: "style-loader!css-loader"
               }, {
                   test: /\.(png|woff|woff2|eot|ttf|svg)$/,
                   loader: 'url-loader?limit=100000'
               }]
       }
};