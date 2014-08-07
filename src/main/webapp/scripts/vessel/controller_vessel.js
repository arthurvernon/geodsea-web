'use strict';

geodseaApp.controller('VesselController', ['$scope', 'resolvedVessel', 'Vessel',
    function ($scope, resolvedVessel, Vessel) {

        $scope.vessels = resolvedVessel;
        $scope.vesselTypes =
            [
                {"name": "BOW_RIDER", "motorised": true},
                {"name": "HALF_CABIN", "motorised": true},
                {"name": "WALK_AROUND", "motorised": true},
                {"name": "CENTER_CONSOLE", "motorised": true},
                {"name": "OPEN_BOAT", "motorised": true},
                {"name": "FLY_BRIDGE", "motorised": true},
                {"name": "PERSONAL_WATER_CRAFT", "motorised": true},
                {"name": "OPEN_COCKPIT ", "motorised": false},
                {"name": "CABIN", "motorised": false},
                {"name": "ROW_BOAT", "motorised": false},
                {"name": "SEA_KAYAK", "motorised": false}
            ];

        $scope.storageTypes =
            [
                {"name": "HOME"},
                {"name": "PEN"},
                {"name": "BOAT_YARD"},
                {"name": "STACKER"},
                {"name": "MOORING"},
                {"name": "JETTY"}
            ]

        $scope.register = function(){
            Vessel.save($scope.vessel, function(){
                window.alert("vessel saved");
                $scope.clear();
                // TODO go somewhere else. Do something.
            });
        };

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
            $scope.vessel = {id: null, vesselName: null, hullIdentificationNumber : null,
                     vesselType: null, vesselLength : null, fuelCapacity: null, storageType: null};
        };
    }]);
