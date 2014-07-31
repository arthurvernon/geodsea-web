'use strict';

geodseaApp.factory('Vessel', ['$resource',
    function ($resource) {
        return $resource('app/rest/vessels/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);


