'use strict';

angular.module('myApp.searchview', [
    'ngRoute',
    'ngStorage',
    'ngSanitize'
])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/search', {
            templateUrl: 'searchview/searchview.html',
            controller: 'SearchCtrl'
        });
    }])
    .controller('SearchCtrl', ['$scope', '$localStorage', '$http', '$location', '$anchorScroll', function ($scope, $localStorage, $http, $location, $anchorScroll) {
        $scope.getSearchResults = function ($searchTerm, $pageNumber) {
            console.log("Got search term: " + $searchTerm);
            if ($searchTerm) {
                var url = $localStorage.serviceUrl + $localStorage.servicePath + '/' + $localStorage.resultsPerPage + '/' + $pageNumber;
                console.log("Calling: " + url);
                $http.defaults.headers.crossDomain = true;
                $http.defaults.headers.common.Accept = 'application/json';
                $http.post(url,
                        {
                            query: $searchTerm,
                            highlightStartTag: '<span class="highlightedTerm">',
                            highlightEndTag: '</span>',
                            queryType: 'KEYWORD'
                        }
                    ).success(function (data, status) {
                        $scope.searchTerm = $searchTerm;
                        $scope.totalNumberOfResults = data.totalNumberOfResults;
                        $scope.searchResults = data.entries;
                        $scope.status = status;
                        $scope.numberOfPages = Math.ceil($scope.totalNumberOfResults / $localStorage.resultsPerPage);
                        $scope.currentPage = $pageNumber;
                        $scope.pages = [];
                        var pageStart = 0;
                        var pageEnd = 9;
                        if (($scope.numberOfPages - 1) > pageEnd) {
                            if (($scope.currentPage + 1) > pageEnd) {
                                pageEnd = $scope.currentPage + 1;
                            }
                            if (($scope.numberOfPages - pageEnd - 1) < 0) {
                                pageEnd += ($scope.numberOfPages - pageEnd - 1);
                            }
                            pageStart = pageEnd - 9;
                        } else {
                            pageEnd = $scope.numberOfPages - 1;
                        }
                        for (var i = pageStart; i <= pageEnd; i++) {
                            $scope.pages.push(i);
                        }
                        $anchorScroll();
                    })
                    .error(function (data, status) {
                        $scope.resetSearch();
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
        $scope.resetSearch = function () {
            $scope.searchTerm = "";
            $scope.searchResults = [];
            $scope.totalNumberOfResults = -1;
            $scope.pages = [];
            $scope.numberOfPages = -1;
            $scope.currentPage = -1;
        };

        $scope.resetSearch();

        $scope.nextPage = function () {
            $scope.currentPage++;
            if ($scope.currentPage == $scope.numberOfPages) {
                $scope.currentPage--;
            }
            $scope.getSearchResults($scope.searchTerm, $scope.currentPage);
        };

        $scope.prevPage = function () {
            $scope.currentPage--;
            if ($scope.currentPage == -1) {
                $scope.currentPage++;
            }
            $scope.getSearchResults($scope.searchTerm, $scope.currentPage);
        }

    }]);
