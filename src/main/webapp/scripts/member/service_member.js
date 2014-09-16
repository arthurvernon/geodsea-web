'use strict';

geodseaApp.factory('Member', ['$resource',
    function ($resource) {
        return $resource('app/rest/members/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);
