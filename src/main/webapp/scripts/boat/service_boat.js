'use strict';

geodseaApp.factory('Boat', ['$resource',
    function ($resource) {
        return $resource('app/rest/boats/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

