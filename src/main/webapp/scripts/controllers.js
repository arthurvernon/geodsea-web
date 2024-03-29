'use strict';

/* Controllers */

geodseaApp.controller('MainController', function ($scope) {
    });

geodseaApp.controller('AdminController', function ($scope) {
    });

geodseaApp.controller('SkipperController', ['$scope',
    function ($scope) {
    }]);

geodseaApp.controller('OwnerController', ['$scope',
    function ($scope) {
    }]);

geodseaApp.controller('RescueController', ['$scope',
    function ($scope) {
    }]);


geodseaApp.controller('HelpController', ['$scope',
    function ($scope) {
    }]);

geodseaApp.controller('LanguageController', function ($scope, $translate, LanguageService) {
        $scope.changeLanguage = function (languageKey) {
            $translate.use(languageKey);

            LanguageService.getBy(languageKey).then(function(languages) {
                $scope.languages = languages;
            });
        };

        LanguageService.getBy().then(function (languages) {
            $scope.languages = languages;
        });
    });

geodseaApp.controller('MenuController', function ($scope) {
    });

geodseaApp.controller('MapController', ['$scope',
    function ($scope) {

        $scope.map = new ol.Map({
            target: 'map',
            layers: [
                new ol.layer.Tile({
                    source: new ol.source.MapQuest({layer: 'sat'})
                })
            ],
            view: new ol.View({
                center: ol.proj.transform([37.41, 8.82], 'EPSG:4326', 'EPSG:3857'),
                zoom: 4
            })
        });
    }]);

geodseaApp.controller('LoginController', function ($scope, $location, AuthenticationSharedService) {
        $scope.rememberMe = true;
        $scope.login = function () {
            AuthenticationSharedService.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            });
        }
    });

geodseaApp.controller('LogoutController', function ($location, AuthenticationSharedService) {
        AuthenticationSharedService.logout();
    });

geodseaApp.controller('SettingsController', function ($scope, Account, $rootScope, Session) {
        $scope.success = null;
        $scope.error = null;

        $scope.settingsAccount = Account.get();

        $scope.save = function () {
            if (typeof($scope.details) != 'undefined') {
                $scope.settingsAccount.addressParts = $scope.details.address_components;
                $scope.settingsAccount.point = {
                    "lat": $scope.details.geometry.location.k,
                    "lon": $scope.details.geometry.location.B
                };
            }

            Account.save($scope.settingsAccount,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                    Account.get(function(data) {
                        $scope.settingsAccount = data;
                        Session.create(data.id, data.login, data.firstName, data.lastName, data.email,
                            data.telephone, data.question, data.answer, data.address, data.roles);
                        $rootScope.account = Session;
                    });
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    });

geodseaApp.controller('RegisterController', function ($scope, $translate, Register) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.register = function () {
            if ($scope.registerAccount.password != $scope.confirmPassword) {
                $scope.doNotMatch = "ERROR";
            } else {
                $scope.registerAccount.langKey = $translate.use();
                $scope.doNotMatch = null;
                $scope.registerAccount.addressParts = $scope.details.address_components;
                $scope.registerAccount.point = {
                    "lat": $scope.details.geometry.location.k,
                    "lon": $scope.details.geometry.location.B
                };
                Register.save($scope.registerAccount,
                    function (value, responseHeaders) {
                        $scope.error = null;
                        $scope.errorUserExists = null;
                        $scope.success = 'OK';
                    },
                    function (httpResponse) {
                        $scope.success = null;
                        if (httpResponse.status === 304 &&
                                httpResponse.data.error && httpResponse.data.error === "Not Modified") {
                            $scope.error = null;
                            $scope.errorUserExists = "ERROR";
                        } else {
                            $scope.error = "ERROR";
                            $scope.errorUserExists = null;
                        }
                    });
            }
        }
    });

geodseaApp.controller('ActivationController', function ($scope, $routeParams, Activate) {
        Activate.get({key: $routeParams.key},
            function (value, responseHeaders) {
                $scope.error = null;
                $scope.success = 'OK';
            },
            function (httpResponse) {
                $scope.success = null;
                $scope.error = "ERROR";
            });
    });

// TODO Password was PasswordChange
geodseaApp.controller('PasswordController', function ($scope, Password) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.changePassword = function () {
            if ($scope.password.newPassword != $scope.confirmPassword) {
                $scope.doNotMatch = "ERROR";
            } else {
                $scope.doNotMatch = null;
                Password.save($scope.password,
                    function (value, responseHeaders) {
                        $scope.error = null;
                        $scope.success = 'OK';
                        $scope.errorcode = null;
                    },
                    function (httpResponse) {
                        $scope.success = null;
                        if (httpResponse.data)
                            $scope.errorcode = "errors."+ httpResponse.data;
                        else
                            $scope.error = "ERROR";
                    });
            }
        };
    });

geodseaApp.controller('SessionsController', function ($scope, resolvedSessions, Sessions) {
        $scope.success = null;
        $scope.error = null;
        $scope.sessions = resolvedSessions;
        $scope.invalidate = function (series) {
            Sessions.delete({series: encodeURIComponent(series)},
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = "OK";
                    $scope.sessions = Sessions.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    });

 geodseaApp.controller('TrackerController', function ($scope) {
        // This controller uses the Atmosphere framework to keep a Websocket connection opened, and receive
        // user activities in real-time.

        $scope.activities = [];
        $scope.trackerSocket = atmosphere;
        $scope.trackerSubSocket;
        $scope.trackerTransport = 'websocket';

        $scope.trackerRequest = { url: 'websocket/tracker',
            contentType : "application/json",
            transport : $scope.trackerTransport ,
            trackMessageLength : true,
            reconnectInterval : 5000,
            enableXDR: true,
            timeout : 60000 };

        $scope.trackerRequest.onOpen = function(response) {
            $scope.trackerTransport = response.transport;
            $scope.trackerRequest.uuid = response.request.uuid;
        };

        $scope.trackerRequest.onMessage = function (response) {
            var message = response.responseBody;
            var activity = atmosphere.util.parseJSON(message);
            var existingActivity = false;
            for (var index = 0; index < $scope.activities.length; index++) {
                if($scope.activities[index].sessionId == activity.sessionId) {
                    existingActivity = true;
                    if (activity.page == "logout") {
                        $scope.activities.splice(index, 1);
                    } else {
                        $scope.activities[index] = activity;
                    }
                }
            }
            if (!existingActivity) {
                $scope.activities.push(activity);
            }
            $scope.$apply();
        };

        $scope.trackerSubSocket = $scope.trackerSocket.subscribe($scope.trackerRequest);
    });

// TODO metricscontroller has had huge changes
geodseaApp.controller('MetricsController', function ($scope, MetricsService, HealthCheckService, ThreadDumpService) {

        $scope.metrics = {};
		$scope.updatingHealth = true;
		$scope.updatingMetrics = true;

        $scope.refresh = function() {
			$scope.updatingHealth = true;
			$scope.updatingMetrics = true;
        	HealthCheckService.check().then(function(promise) {
        		$scope.healthCheck = promise;
				$scope.updatingHealth = false;
        	},function(promise) {
        		$scope.healthCheck = promise.data;
				$scope.updatingHealth = false;
        	});

			MetricsService.get().then(function(promise) {
        		$scope.metrics = promise;
				$scope.updatingMetrics = false;
        	},function(promise) {
        		$scope.metrics = promise.data;
				$scope.updatingMetrics = false;
        	});


        };

		$scope.$watch('metrics', function(newValue, oldValue) {
			$scope.servicesStats = {};
            $scope.cachesStats = {};
            angular.forEach(newValue.timers, function(value, key) {
                if (key.indexOf("web.rest") != -1 || key.indexOf("service") != -1) {
                    $scope.servicesStats[key] = value;
                }

                if (key.indexOf("net.sf.ehcache.Cache") != -1) {
                    // remove gets or puts
                    var index = key.lastIndexOf(".");
                    var newKey = key.substr(0, index);

                    // Keep the name of the domain
                    index = newKey.lastIndexOf(".");
                    $scope.cachesStats[newKey] = {
                        'name': newKey.substr(index + 1),
                        'value': value
                    };
                };
            });
		});

        $scope.refresh();

        $scope.threadDump = function() {
            ThreadDumpService.dump().then(function(data) {
                $scope.threadDump = data;

                $scope.threadDumpRunnable = 0;
                $scope.threadDumpWaiting = 0;
                $scope.threadDumpTimedWaiting = 0;
                $scope.threadDumpBlocked = 0;

                angular.forEach(data, function(value, key) {
                    if (value.threadState == 'RUNNABLE') {
                        $scope.threadDumpRunnable += 1;
                    } else if (value.threadState == 'WAITING') {
                        $scope.threadDumpWaiting += 1;
                    } else if (value.threadState == 'TIMED_WAITING') {
                        $scope.threadDumpTimedWaiting += 1;
                    } else if (value.threadState == 'BLOCKED') {
                        $scope.threadDumpBlocked += 1;
                    }
                });

                $scope.threadDumpAll = $scope.threadDumpRunnable + $scope.threadDumpWaiting +
                    $scope.threadDumpTimedWaiting + $scope.threadDumpBlocked;

            });
        };

        $scope.getLabelClass = function(threadState) {
            if (threadState == 'RUNNABLE') {
                return "label-success";
            } else if (threadState == 'WAITING') {
                return "label-info";
            } else if (threadState == 'TIMED_WAITING') {
                return "label-warning";
            } else if (threadState == 'BLOCKED') {
                return "label-danger";
            }
        };
    });

geodseaApp.controller('LogsController', function ($scope, resolvedLogs, LogsService) {
        $scope.loggers = resolvedLogs;

        $scope.changeLevel = function (name, level) {
            LogsService.changeLevel({name: name, level: level}, function () {
                $scope.loggers = LogsService.findAll();
            });
        }
    });

geodseaApp.controller('AuditsController', function ($scope, $translate, $filter, AuditsService) {
        $scope.onChangeDate = function() {
            AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function(data){
                $scope.audits = data;
            });
        };

        // Date picker configuration
        $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            var tomorrow = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1); // create new increased date

            $scope.toDate = $filter('date')(tomorrow, "yyyy-MM-dd");
        };

        $scope.previousMonth = function() {
            var fromDate = new Date();
            if (fromDate.getMonth() == 0) {
                fromDate = new Date(fromDate.getFullYear() - 1, 0, fromDate.getDate());
            } else {
                fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
            }

            $scope.fromDate = $filter('date')(fromDate, "yyyy-MM-dd");
        };

        $scope.today();
        $scope.previousMonth();

        AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function(data){
            $scope.audits = data;
        });
    });

