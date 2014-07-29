'use strict';

geodseaApp.controller('VesselController', ['$scope', 'resolvedVessel', 'Vessel',
    function ($scope, resolvedVessel, Boat) {

        $scope.vessels = resolvedVessel;

        $scope.create = function () {
            Vessel.save($scope.vessel,
                function () {
                    $scope.vessels = Boat.query();
                    $('#saveVesselModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.vessel = Vessel.get({id: id});
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
