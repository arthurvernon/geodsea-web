'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/skipper', {
                    templateUrl: 'views/skippers.html',
                    controller: 'SkipperController',
                    resolve:{
                        resolvedSkipper: ['Skipper', function (Skipper) {
                            return Skipper.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
