'use strict';

geodseaApp.controller('SkipperController', ['$scope', 'resolvedSkipper', 'Skipper',
    function ($scope, resolvedSkipper, Skipper) {

        $scope.skippers = resolvedSkipper;

        $scope.create = function () {
            Skipper.save($scope.skipper,
                function () {
                    $scope.skippers = Skipper.query();
                    $('#saveSkipperModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.skipper = Skipper.get({id: id});
            $('#saveSkipperModal').modal('show');
        };

        $scope.delete = function (id) {
            Skipper.delete({id: id},
                function () {
                    $scope.skippers = Skipper.query();
                });
        };

        $scope.clear = function () {
            $scope.skipper = {id: null, sampleTextAttribute: null, sampleDateAttribute: null};
        };
    }]);
