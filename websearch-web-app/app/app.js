'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'ngStorage',
    'myApp.searchview',
    'myApp.configview',
    'myApp.version',
    'myApp.adminview',
    'myApp.rabbit',
    'myapp.Auth',
    'ngSanitize'
    ])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.otherwise({redirectTo: '/search'});
    }])
    .controller('MainCtrl', ['$rootScope', '$scope', '$location', '$localStorage', function ($rootScope, $scope, $location, $localStorage) {
        $rootScope.go = function (path) {
            $location.path(path);
        };
        $localStorage.$default(
            {
                serviceUrl: "http://localhost:8080",
                servicePath: "/search",
                resultsPerPage: 10,
                searchType: "FUZZY",
                searchParams: []
            }
        );

    }]);
