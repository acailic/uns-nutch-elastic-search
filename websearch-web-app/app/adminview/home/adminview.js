'use strict';

angular.module('myApp.adminview', [
    'ngStorage',
    'ngSanitize'
])
    .controller('AdminCtrl', ['$scope', '$localStorage', '$http', '$location', function ($scope, $localStorage, $http, $location) {
        $scope.postNutch = function ($phase, $cycles,  $inject, $topn, $seedUrl) {
            console.log("Got seedUrl: " + $seedUrl);
            if ($validated) {
                var url = $localStorage.serviceUrl + $localStorage.nutchPath;
                console.log("Calling: " + url);
                $http.defaults.headers.crossDomain = true;
                $http.defaults.headers.common.Accept = 'application/json';
                $http.post(url,
                        { 
                            inject: $inject,
                            stage: $stage,
                            cycles: $cycles,
                            topn: $topn,
                            seedList: $seedUrl,
                        }
                    ).success(function (data, status) {
                       $scope.status = status;
                       $.notify("Request succeed with status: " + $scope.status,
                            {
                                position: "top center",
                                className: "error"
                            }
                        );
                    })
                    .error(function (data, status) { 
                        $scope.status = status;
                        $.notify("Request failed with status: " + $scope.status,
                            {
                                position: "top center",
                                className: "error"
                            }
                        );
                    });

            }
        }; 
  
    }]);
