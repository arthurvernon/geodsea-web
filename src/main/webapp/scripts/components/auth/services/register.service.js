'use strict';

angular.module('geodseaApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


