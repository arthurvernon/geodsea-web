'use strict';

geodseaApp.controller('BoatController', ['$scope', 'resolvedBoat', 'Boat',
    function ($scope, resolvedBoat, Boat) {

        $scope.boats = resolvedBoat;

        $scope.create = function () {
            Boat.save($scope.boat,
                function () {
                    $scope.boats = Boat.query();
                    $('#saveBoatModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.boat = Boat.get({id: id});
            $('#saveBoatModal').modal('show');
        };

        $scope.delete = function (id) {
            Boat.delete({id: id},
                function () {
                    $scope.boats = Boat.query();
                });
        };

        $scope.clear = function () {
            $scope.boat = {id: null, sampleTextAttribute: null, sampleDateAttribute: null};
        };
    }]);
