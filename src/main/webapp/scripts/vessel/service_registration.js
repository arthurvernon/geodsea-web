'use strict';

geodseaApp.factory('VesselRegistration', ['$resource',
    function ($resource) {
        return $resource('app/rest/registrations/:number', {}, {
            'get': { method: 'GET'}
        });
    }]);

