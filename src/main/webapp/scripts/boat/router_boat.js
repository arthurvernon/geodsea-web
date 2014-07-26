'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/boat', {
                    templateUrl: 'views/boats.html',
                    controller: 'BoatController',
                    resolve:{
                        resolvedBoat: ['Boat', function (Boat) {
                            return Boat.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
