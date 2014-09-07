'use strict';

geodseaApp.controller('GroupController', ['$scope', 'resolvedGroup', 'Group',
    function ($scope, resolvedGroup, Group) {

        $scope.groups = resolvedGroup;

        $scope.create = function () {
            Group.save($scope.group,
                function () {
                    $scope.groups = Group.query();
                    $('#saveGroupModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.group = Group.get({id: id});
            $('#saveGroupModal').modal('show');
        };

        $scope.delete = function (id) {
            Group.delete({id: id},
                function () {
                    $scope.groups = Group.query();
                });
        };

        $scope.clear = function () {
            $scope.group = {id: null, login: null, email: null};
        };
    }]);

geodseaApp.controller('GroupRegisterController', ['$scope', 'Group',
    function ($scope, Group) {

        $scope.registerGroup = function () {
            $scope.error = null;
            $scope.errorUserExists = null;
            $scope.success = null;

            // update address details only if they are present.
            if (typeof($scope.details) != 'undefined') {
                $scope.registerGroup.addressParts = $scope.details.address_components;
                $scope.registerGroup.point = {
                    "lat": $scope.details.geometry.location.k,
                    "lon": $scope.details.geometry.location.B
                };
            }

            Group.save($scope.group,
                function () {
                    $scope.error = null;
                    $scope.errorUserExists = null;
                    $scope.success = 'OK';

                },
                function (httpResponse) {
                    $scope.success = null;
                    if (httpResponse.status === 304) {
                        $scope.error = null;
                        $scope.errorUserExists = "ERROR";
                    } else {
                        $scope.error = "ERROR";
                        $scope.errorUserExists = null;
                    }
                });
        };

        $scope.update = function (id) {
            $scope.group = Group.get({id: id});
            $('#saveGroupModal').modal('show');
        };

        $scope.clear = function () {
            $scope.group = {id: null, login: null, email: null};
        };
    }]);
