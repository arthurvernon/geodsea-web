'use strict';

geodseaApp.factory('Skipper', ['$resource',
    function ($resource) {
        return $resource('app/rest/skippers/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);
