'use strict';

geodseaApp.controller('VesselController', ['$scope', 'resolvedVessel', 'Vessel',
    function ($scope, resolvedVessel, Vessel) {

        $scope.vessels = resolvedVessel;
        $scope.vesselTypes =
            [
                {"id": "BOW_RIDER", "motorised": true},
                {"id": "HALF_CABIN", "motorised": true},
                {"id": "WALK_AROUND", "motorised": true},
                {"id": "CENTER_CONSOLE", "motorised": true},
                {"id": "OPEN_BOAT", "motorised": true},
                {"id": "FLY_BRIDGE", "motorised": true},
                {"id": "PERSONAL_WATER_CRAFT", "motorised": true},
                {"id": "OPEN_COCKPIT ", "motorised": false},
                {"id": "CABIN", "motorised": false},
                {"id": "ROW_BOAT", "motorised": false},
                {"id": "SEA_KAYAK", "motorised": false}
            ];


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
