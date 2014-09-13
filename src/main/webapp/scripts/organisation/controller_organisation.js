'use strict';

geodseaApp.controller('OrganisationController', ['$scope', 'resolvedOrganisation', 'Organisation',
    function ($scope, resolvedOrganisation, Organisation) {

        $scope.organisations = resolvedOrganisation;

        $scope.create = function () {
            Organisation.save($scope.organisation,
                function () {
                    $scope.organisations = Organisation.query();
                    $('#saveOrganisationModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.organisation = Organisation.get({id: id});
            $('#saveOrganisationModal').modal('show');
        };

        $scope.delete = function (id) {
            Organisation.delete({id: id},
                function () {
                    $scope.organisations = Organisation.query();
                });
        };

        $scope.clear = function () {
            $scope.organisation = {id: null, sampleTextAttribute: null, sampleDateAttribute: null};
        };
    }]);
