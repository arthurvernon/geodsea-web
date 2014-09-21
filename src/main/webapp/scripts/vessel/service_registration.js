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


/*
 * Access the groups that this user is an active manager of
 */
geodseaApp.factory('ManagedOrganisations', ['$resource',
    function ($resource) {
        return $resource('app/rest/organsations/user/manages', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }]);
