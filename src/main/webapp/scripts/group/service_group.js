'use strict';

geodseaApp.factory('Group', ['$resource',
    function ($resource) {
        return $resource('app/rest/groups/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);
