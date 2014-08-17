'use strict';

/**
 * The conventional licensor factory for CRUD operations.
 */
geodseaApp.factory('Licensor', ['$resource',
    function ($resource) {
        return $resource('app/rest/licensors/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

/**
 * Mechanism to get the licensing agency that is closest to the user's
 * home address.
 * Home address must presently be within an area defined by the licensing agency.
 */
geodseaApp.factory('LicensorUserMatch', ['$resource',
    function ($resource) {
        return $resource('app/rest/userlicensor/:username', {}, {
            'get': { method: 'GET'}
        });
    }]);

/**
 * Serivce to obtain the license details from a licensro via a web service lookup.
 */
geodseaApp.factory('LicensorLicenseLookup', ['$resource',
    function ($resource) {
        return $resource('app/rest/licensor/:id/registration/:reg', {}, {
            'get': { method: 'GET'}
        });
    }]);
