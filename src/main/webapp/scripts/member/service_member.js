'use strict';

// CRUD operations on members within a collective. Gets members of all groups so not useful to general users.
geodseaApp.factory('Member', ['$resource',
    function ($resource) {
        return $resource('app/rest/members/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

