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
        return $resource('app/rest/organisations/user/manages', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }]);

/*
 * Access the groups that this user is an active manager of
 */
geodseaApp.factory('Friends', ['$resource',
    function ($resource) {
        return $resource('app/rest/friends', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }]);
