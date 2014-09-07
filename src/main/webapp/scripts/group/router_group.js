'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/group', {
                    templateUrl: 'views/groups.html',
                    controller: 'GroupController',
                    resolve:{
                        resolvedGroup: ['Group', function (Group) {
                            return Group.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/register/group', {
                    templateUrl: 'views/registergroup.html',
                    controller: 'GroupRegisterController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })

        }]);
