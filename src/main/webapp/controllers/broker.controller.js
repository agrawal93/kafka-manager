var app = angular.module("kafka-manager");

app.controller("BrokerController", function ($scope, BrokerService, filterFilter) {

    $scope.brokers = BrokerService.getAll();

    $scope.pageNo = 1;
    $scope.limit = 15;

    $scope.getKeys = function () {
        return Object.keys($scope.brokers);
    };

    $scope.getBroker = function (brokerId) {
        return BrokerService.get(brokerId);
    };

    $scope.changePage = function (pageNo) {
        $scope.pageNo = pageNo;
    };

    $scope.changeLimit = function () {
        if (!this.limit)
            this.limit = $scope.limit;
    };

});