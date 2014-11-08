'use strict';

geodseaApp.controller('SkipperTripController', ['$scope', '$location', 'resolvedSkipperTrip', 'SkipperTrip', 'MAPCONSTANTS', 'TripService',
    function ($scope, $location, resolvedSkipperTrip, SkipperTrip, MAPCONSTANTS, TripService) {

        $scope.errorcode = null;
        $scope.error = null;
        $scope.trips = resolvedSkipperTrip;

        $scope.update = function (id) {
            SkipperTrip.get({id: id, srid: MAPCONSTANTS.SRID3857},
                function(resp){
                    TripService.setTrip(resp);
                    $location.path('/skipper/trip');
                });
        };

        $scope.delete = function (id) {
            SkipperTrip.delete({id: id},
                function () {
                    $scope.trips = SkipperTrip.query();
                });
        };

        $scope.newTrip= function () {
            TripService.resetTrip();
            $location.path('/skipper/trip')
        };

    }]);


