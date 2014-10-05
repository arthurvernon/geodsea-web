'use strict';

geodseaApp.factory('SkipperTrip', ['$resource',
    function ($resource) {
        return $resource('app/rest/skipper/trips/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET',
                transformResponse: function(data, headerFn)
                {
                    var obj = JSON.parse(data);
                    convertDateStringsToDates(obj);
                    return obj;
                }}
        });
    }]);

geodseaApp.factory('SkipperVessel', ['$resource',
    function ($resource) {
        return $resource('app/rest/skipper/vessels/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

//var regexIso8601 = /^(\d{4}|\+\d{6})(?:-(\d{2})(?:-(\d{2})(?:T(\d{2}):(\d{2}):(\d{2})\.(\d{1,})(Z|([\-+])(\d{2}):(\d{2}))?)?)?)?$/;

function stringEndsWith(str, suffix) {
    var l = str.length - suffix.length;
    return l >= 0 && str.indexOf(suffix, l) == l;
};

function convertDateStringsToDates(input) {
    // Ignore things that aren't objects.

    if (typeof input !== "object") return input;

    for (var key in input) {
        if (!input.hasOwnProperty(key)) continue;

        var value = input[key];

        // Check for string properties which look like dates.
        if (stringEndsWith(key, "_dt") && typeof value === "number") {
                input[key] = new Date(value);
        } else if (typeof value === "object") {
            // Recurse into object
            convertDateStringsToDates(value);
        }
    }
}
