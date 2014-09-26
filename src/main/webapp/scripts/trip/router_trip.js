'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/trip', {
                    templateUrl: 'views/trips.html',
                    controller: 'TripController',
                    resolve:{
                        resolvedTrip: ['Trip', function (Trip) {
                            return Trip.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
