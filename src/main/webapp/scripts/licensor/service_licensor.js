'use strict';

geodseaApp.factory('Licensor', ['$resource',
    function ($resource) {
        return $resource('app/rest/licensors/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

geodseaApp.factory('UserLicensor', ['$resource',
    function ($resource) {
        return $resource('app/rest/userlicensor/:username', {}, {
            'get': { method: 'GET'}
        });
    }]);
