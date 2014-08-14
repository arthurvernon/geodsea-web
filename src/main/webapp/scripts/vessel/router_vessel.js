'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/vessel', {
                    templateUrl: 'views/vessels.html',
                    controller: 'VesselController',
                    resolve: {
                        vesselList: ['Vessel', function (Vessel) {
                            return Vessel.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/register/vessel', {
                    templateUrl: 'views/registervessel.html',
                    controller: 'VesselRegistrationController',
                    resolve: {
                        licensorList: ['Licensor', function (Licensor) {
                            return Licensor.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
