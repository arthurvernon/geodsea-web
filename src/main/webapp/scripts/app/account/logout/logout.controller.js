'use strict';

angular.module('geodseaApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
