'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/rescue', {
                    templateUrl: 'views/rescue.html',
                    controller: 'RescueController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
