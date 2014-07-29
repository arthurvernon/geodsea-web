'use strict';

geodseaApp.controller('VesselController', ['$scope', 'resolvedVessel', 'Vessel',
    function ($scope, resolvedVessel, Vessel) {

        $scope.vessels = resolvedVessel;

        $scope.create = function () {
            Vessel.save($scope.vessel,
                function () {
                    $scope.vessels = Vessel.query();
                    $('#saveVesselModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.vessel = Vessel.get({id: id});
            window.alert('hello');
            $('#saveVesselModal').modal('show');
        };

        $scope.delete = function (id) {
            Vessel.delete({id: id},
                function () {
                    $scope.vessels = Vessel.query();
                });
        };

        $scope.clear = function () {
            $scope.vessel = {id: null, sampleTextAttribute: null, sampleDateAttribute: null};
        };
    }]);
