'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/vessel', {
                    templateUrl: 'views/vessels.html',
                    controller: 'VesselController',
                    resolve:{
                        resolvedVessel: ['Vessel', function (Vessel) {
                            return Vessel.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/register/vessel', {
                    templateUrl: 'views/registervessel.html',
                    controller: 'VesselController',
                    resolve:{
                        resolvedVessel: ['Vessel', function (Vessel) {
                            return Vessel.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
