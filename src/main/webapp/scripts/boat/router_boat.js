'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/boat', {
                    templateUrl: 'views/vessels.html',
                    controller: 'VesselController',
                    resolve:{
                        resolvedBoat: ['Vessel', function (Vessel) {
                            return Vessel.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
