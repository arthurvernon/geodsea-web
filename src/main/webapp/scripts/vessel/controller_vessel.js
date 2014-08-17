'use strict';

geodseaApp.controller('VesselController', ['$scope', 'vesselList', 'Vessel',
    function ($scope, vesselList, Vessel) {

        $scope.vessels = vesselList;

        $scope.create = function () {
            Vessel.save($scope.vessel,
                function () {
                    $scope.vessels = Vessel.query();
                    $('#saveVesselModal').modal('hide');
                    $scope.clear();
                },
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
            $scope.vessel = {id: null, vesselName: null, hullIdentificationNumber: null,
                vesselType: null, length: null, fuelCapacity: null, storageType: null};
        };

    }]);


geodseaApp.controller('VesselRegistrationController', ['$scope', 'Vessel', 'VesselRegistration', 'LicensorUserMatch', 'Licensor',
    'licensorList',
    function ($scope, Vessel, VesselRegistration, LicensorUserMatch, Licensor, licensorList) {

        $scope.licensorList = licensorList;
        $scope.registration = null;

        $scope.register = function () {
            Vessel.save($scope.vessel, function () {
                    window.alert("Vessel Registered")
                    //       $scope.clear();
                    // TODO go somewhere else. Do something.
                },
                function () {
                    window.alert("Vessel Registration failed")
                    //       $scope.clear();
                    // TODO go somewhere else. Do something.
                });
        };

        $scope.populate = function () {
            VesselRegistration.get($scope.registration.number, function () {
                    window.alert('got registration details from license');
                },
                function () {
                    window.alert('failed to load registration details for license');
                });
        }

        $scope.enterRegistration = function () {
            $('#enterRegistrationModal').modal('hide');

        };

        /*
         * Lookup a specific license by calling the underlying web service
         * Of that licensing agency
         */
        $scope.lookup = function (licensorid, registrationnumber) {

            window.alert('lookup license ' + registrationnumber + ' on licensor ' + licensorid);
        };

        /*
         * load up all the licensors into the licensorList object
         */
        $scope.loadLicensors = function () {
            Licensor.get(function (list) {
                $scope.licensorList = list;
            });
        };

        $scope.clear = function () {
            $scope.checked = null;
            LicensorUserMatch.get({username: $scope.account.login}, function (licensor) {
                    $scope.licensor = licensor;
                },
                function () {
                    window.alert('failed to load registration details for license');
                });

            $scope.licensor = {id: null, name: null};
        };

    }]);
