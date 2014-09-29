'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/skipper/trips', {
                    templateUrl: 'views/skippertrips.html',
                    controller: 'SkipperTripController',
                    resolve:{
                        vessels: ['SkipperVessel', function(SkipperVessel){
                            return SkipperVessel.query();
                        }],
                        resolvedSkipperTrip: ['SkipperTrip', function (SkipperTrip) {
                            return SkipperTrip.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.ROLE_SKIPPER]
                    }
                })
        }]);
