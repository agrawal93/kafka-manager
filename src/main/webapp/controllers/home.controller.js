var app = angular.module("kafka-manager");

app.controller("HomeController", function ($rootScope, $scope, ClusterService, BrokerService, TopicService) {

    $scope.modal = function (modalName) {
        $(modalName).modal().modal('open');
    };

    $scope.selectCluster = function (clusterId, init) {
        if (!init && $rootScope.selectedCluster && $rootScope.selectedCluster === clusterId)
            return;

        $scope.brokerPromise = BrokerService.init(clusterId)
                .then(function () {
                    $rootScope.selectedCluster = clusterId;
                    $rootScope.brokerPages = BrokerService.getPages();
                });

        $scope.topicPromise = TopicService.init(clusterId)
                .then(function () {
                    $rootScope.topicPages = TopicService.getPages();
                });

        return $scope.brokerPromise;
    };

    if ($rootScope.initialized === false) {
        $rootScope.initialized = true;
        ClusterService.initPromise
                .then(function () {
                    $rootScope.selectedCluster = Object.keys(ClusterService.getAll())[0];
                    $scope.selectCluster($rootScope.selectedCluster, true)
                            .then(function () {
                                $('.tabs').tabs();
                                $('.modal').modal();
                                $('.collapsible').collapsible();
                                $("#cluster-nav .collapsible").collapsible('open', 1);
                            });
                });
    }

});