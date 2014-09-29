'use strict';

geodseaApp.factory('SkipperTrip', ['$resource',
    function ($resource) {
        return $resource('app/rest/skipper/trips/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);
