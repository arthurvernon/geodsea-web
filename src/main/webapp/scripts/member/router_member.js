'use strict';

geodseaApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/member', {
                    templateUrl: 'views/members.html',
                    controller: 'MemberController',
                    resolve:{
                        resolvedMember: ['Member', function (Member) {
                            return Member.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
