'use strict';

geodseaApp.controller('GroupController', ['$scope', 'resolvedGroup', 'Group','$translate','GroupMember',
    function ($scope, resolvedGroup, Group, $translate, GroupMember) {

        $scope.groups = resolvedGroup;
        $scope.create = function () {
            if (! $scope.group.id) {
                $scope.group.langKey = $translate.use();
            }
            Group.save($scope.group,
                function () {
                    $scope.groups = Group.query();
                    $('#saveGroupModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.members = GroupMember.query({groupId: id});
            $scope.group = Group.get({id: id});
            $('#saveGroupModal').modal('show');
        };

        $scope.delete = function (id) {
            Group.delete({id: id},
                function () {
                    $scope.groups = Group.query();
                });
        };

        $scope.closing = function () {
            $scope.members = null;
            $scope.group = {id: null, login: null, email: null};
        };

        $scope.adding = function () {
            $scope.group = {id: null, login: null, email: null};
        };
    }]);

geodseaApp.controller('GroupRegisterController', ['$scope', 'Group', '$translate',
    function ($scope, Group, $translate) {

        $scope.registerGroup = function () {
            $scope.error = null;
            $scope.errorUserExists = null;
            $scope.success = null;

            // update address details only if they are present.
            if (typeof($scope.details) != 'undefined') {
                $scope.group.addressParts = $scope.details.address_components;
                $scope.group.point = {
                    "lat": $scope.details.geometry.location.k,
                    "lon": $scope.details.geometry.location.B
                };

            }

            $scope.group.langKey = $translate.use();

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
                        if (httpResponse.data)
                            $scope.errorcode = "errors."+ httpResponse.data;
                        else
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
            $scope.group = {id: null, login: null, email: null, contact: null, contactPerson: null};
        };
    }]);
