var app = angular.module("kafka-manager");

app.controller("TopicController", function ($scope, TopicService, filterFilter) {

    $scope.topics = TopicService.getAll();

    $scope.pageNo = 1;
    $scope.limit = 15;
    $scope.filteredTopics = filterFilter(Object.keys($scope.topics), this.searchTopic);

    $scope.getKeys = function () {
        $scope.filteredTopics = filterFilter(Object.keys($scope.topics), this.searchTopic);
    };

    $scope.getTopic = function (topicName) {
        return TopicService.get(topicName);
    };

    $scope.changeSearch = function () {
        $scope.pageNo = 1;
        $scope.filteredTopics = filterFilter(Object.keys($scope.topics), this.searchTopic);
    };

    $scope.changePage = function (pageNo) {
        $scope.pageNo = pageNo;
    };

    $scope.changeLimit = function () {
        if (!this.limit)
            this.limit = $scope.limit;
    };

});
