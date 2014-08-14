'use strict';

geodseaApp.controller('LicensorController', ['$scope', 'resolvedLicensor', 'Licensor',
    function ($scope, resolvedLicensor, Licensor) {

        $scope.licensors = resolvedLicensor;

        $scope.create = function () {
            Licensor.save($scope.licensor,
                function () {
                    $scope.licensors = Licensor.query();
                    $('#saveLicensorModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.licensor = Licensor.get({id: id});
            $('#saveLicensorModal').modal('show');
        };

        $scope.delete = function (id) {
            Licensor.delete({id: id},
                function () {
                    $scope.licensors = Licensor.query();
                });
        };

        $scope.clear = function () {
            $scope.licensor = {id: null, participantGroupId: null, groupName: null, webServiceURL : null, region : null};
        };
    }]);
