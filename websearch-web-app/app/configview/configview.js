'use strict';

angular.module('myApp.configview', [
    'ngRoute',
    'ngStorage',
    'ngSanitize'
    ])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/config', {
            templateUrl: 'configview/configview.html',
            controller: 'ConfigCtrl'
        });
    }])
    .controller('ConfigCtrl', ['$scope', '$localStorage', '$location', function ($scope, $localStorage, $location) {
        $scope.$storage = $localStorage;

        $scope.servicePath = $localStorage.servicePath;
        $scope.serviceUrl = $localStorage.serviceUrl;
        $scope.resultsPerPage = $localStorage.resultsPerPage;

        $scope.restoreDefaults = function () {
            console.log("Restoring settings to defaults");
            $scope.servicePath = "/search";
            $scope.serviceUrl = "http://localhost:8080";
            $scope.resultsPerPage = 10;
            $.notify("Restored to defaults.",
                {
                    position: "top center",
                    className: "info"
                }
            );
        };

        $scope.saveSettings = function () {
            $localStorage.servicePath = $scope.servicePath;
            $localStorage.serviceUrl = $scope.serviceUrl;
            $localStorage.resultsPerPage = $scope.resultsPerPage;
            $.notify("Settings saved.",
                {
                    position: "top center",
                    className: "success"
                }
            );
            $location.path('search')
        };
    }]);