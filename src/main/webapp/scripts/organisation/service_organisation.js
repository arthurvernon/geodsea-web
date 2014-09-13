'use strict';

geodseaApp.factory('Organisation', ['$resource',
    function ($resource) {
        return $resource('app/rest/organisations/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);
