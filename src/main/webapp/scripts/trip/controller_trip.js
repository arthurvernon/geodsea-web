'use strict';

geodseaApp.controller('TripController', ['$scope', 'resolvedTrip', 'Trip',
    function ($scope, resolvedTrip, Trip) {

        $scope.trips = resolvedTrip;

        $scope.create = function () {
            Trip.save($scope.trip,
                function () {
                    $scope.trips = Trip.query();
                    $('#saveTripModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.trip = Trip.get({id: id});
            $('#saveTripModal').modal('show');
        };

        $scope.delete = function (id) {
            Trip.delete({id: id},
                function () {
                    $scope.trips = Trip.query();
                });
        };

        $scope.clear = function () {
            $scope.trip = {id: null, sampleTextAttribute: null, sampleDateAttribute: null};
        };
    }]);
