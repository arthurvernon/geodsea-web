// Karma configuration
// Generated on Tue Apr 07 2015 19:23:50 GMT+0800 (W. Australia Standard Time)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
        'src/main/webapp/bower_components/openlayers/ol4.min.js',
        'src/main/webapp/bower_components/modernizr/modernizr.js',
        'src/main/webapp/bower_components/jquery/dist/jquery.js',
        'src/main/webapp/bower_components/angular/angular.js',
        'src/main/webapp/bower_components/angular-mocks/angular-mocks.js',
        'src/main/webapp/bower_components/angular-route/angular-route.js',
        'src/main/webapp/bower_components/angular-resource/angular-resource.js',
        'src/main/webapp/bower_components/angular-cookies/angular-cookies.js',
        'src/main/webapp/bower_components/angular-sanitize/angular-sanitize.js',
        'src/main/webapp/bower_components/angular-translate/angular-translate.js',
        'src/main/webapp/bower_components/angular-translate-storage-cookie/angular-translate-storage-cookie.js',
        'src/main/webapp/bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.js',
        'src/main/webapp/bower_components/angular-dynamic-locale/src/tmhDynamicLocale.js',
        'src/main/webapp/bower_components/ngAutocomplete/src/ngAutocomplete.js',
        'src/main/webapp/scripts/*.js',
        'src/main/webapp/scripts/**/*.js',
        'src/test/javascript/**/!(karma.conf).js',
        'src/main/webapp/views/main.html' // ????
    ],


    // list of files to exclude
    exclude: [

    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {

    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
//    browsers: ['Chrome', 'IE'],
      browsers: ['PhantomJS'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};
