'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/licensor', {
                    templateUrl: 'views/licensors.html',
                    controller: 'LicensorController',
                    resolve:{
                        resolvedLicensor: ['Licensor', function (Licensor) {
                            return Licensor.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
