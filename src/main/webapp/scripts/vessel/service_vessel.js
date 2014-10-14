'use strict';

/**
 * All the vessels or that vessel with the specified vessel ID
 */
geodseaApp.factory('Vessel', ['$resource',
    function ($resource) {
        return $resource('app/rest/vessels/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    }]);

/*
All the skippers for a specified vessel ID.
 */
geodseaApp.factory('VesselSkipper', ['$resource',
    function ($resource) {
        return $resource('app/rest/vessel/:vesselId/skippers', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }]);


/*
 All the skippers of the vessel being used on a skipper trip.
 */
geodseaApp.factory('TripVesselSkipper', ['$resource',
    function ($resource) {
        return $resource('app/rest/trips/:tripId/vessel/skippers', {}, {
            'query': { method: 'GET', isArray: true}
        });
    }]);

