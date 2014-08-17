'use strict';
/*
 * CRUD for vessel registration objects.
 */
geodseaApp.factory('VesselRegistration', ['$resource',
    function ($resource) {
        return $resource('app/rest/registrations/:number', {}, {
            'get': { method: 'GET'}
        });
    }]);

