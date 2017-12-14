var app = angular.module("kafka-manager", ['ngRoute', 'ui.materialize', 'cgBusy']);

app.config(function ($routeProvider) {
    $routeProvider
            .when("/", {templateUrl: "views/home.html", controller: "HomeController"})
            .when("/clusters", {templateUrl: "views/clusters.html", controller: "ClusterController"})
            .when("/brokers", {templateUrl: "views/brokers.html", controller: "BrokerController"})
            .when("/topics", {templateUrl: "views/topics.html", controller: "TopicController"})
            .otherwise({redirectTo: "/"});
}).run(function ($rootScope) {
    $rootScope.initialized = false;
    $rootScope.selectedCluster = null;
    $rootScope.appContext = "/";

    $rootScope.internet = true;
});