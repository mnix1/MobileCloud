var path = require('path');
var webpack = require('webpack');
var jsonImporter = require("node-sass-json-importer");
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
               {test: /\.json$/, loader: 'json-loader'},
               {
                   test: /\.css$/,
                   loader: "style-loader!css-loader"
               }, {
                   test: /\.scss$/,
                   loaders: [
                       {loader: "style-loader"},
                       {
                           loader: "css-loader",
                           options: {sourceMap: true}
                       },
                       {
                           loader: "sass-loader",
                           options: {
                               sourceMap: true,
                               importer: jsonImporter,
                               includePaths: [path.resolve(__dirname, 'src/stylesheets')]
                           }
                       }]
               }, {
                   test: /\.(png|woff|woff2|eot|ttf|svg)$/,
                   loader: 'url-loader?limit=100000'
               }]
       }
};