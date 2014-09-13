'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/organisation', {
                    templateUrl: 'views/organisations.html',
                    controller: 'OrganisationController',
                    resolve:{
                        resolvedOrganisation: ['Organisation', function (Organisation) {
                            return Organisation.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
