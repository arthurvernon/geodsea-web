'use strict';

geodseaApp.controller('MemberController', ['$scope', 'resolvedMember', 'Member',
    function ($scope, resolvedMember, Member) {

        $scope.members = resolvedMember;

        $scope.create = function () {
            Member.save($scope.member,
                function () {
                    $scope.members = Member.query();
                    $('#saveMemberModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.member = Member.get({id: id});
            $('#saveMemberModal').modal('show');
        };

        $scope.delete = function (id) {
            Member.delete({id: id},
                function () {
                    $scope.members = Member.query();
                });
        };

        $scope.clear = function () {
            $scope.member = {id: null, sampleTextAttribute: null, sampleDateAttribute: null};
        };
    }]);
