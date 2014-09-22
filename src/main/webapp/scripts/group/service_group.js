'use strict';

geodseaApp.factory('Group', ['$resource',
    function ($resource) {
        return $resource('app/rest/groups/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

// Get the members within the specified group
geodseaApp.factory('GroupMember', ['$resource',
    function ($resource) {
        return $resource('app/rest/groups/:login/members', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

// Get the members within the specified group
geodseaApp.factory('CollectivePeople', ['$resource',
    function ($resource) {
        return $resource('app/rest/groups/:login/people', {}, {
            'query': { method: 'GET', isArray: true},
        });
    }]);
