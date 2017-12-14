var app = angular.module("kafka-manager");

app.factory("TopicService", function ($rootScope, $http, PaginationService) {
    var topicService = new BaseService();

    topicService.init = function (clusterId) {
        return $http
                .get($rootScope.appContext + "clusters/" + clusterId + "/topics")
                .then(function (response) {
                    if (response.status === 200) {
                        topicService.reset();
                        topicService.setCluster(clusterId);
                        topicService.setData(response.data, true, true, PaginationService);
                    }
                });
    };

    var webSocket = new WebSocket("ws://" + window.location.host + $rootScope.appContext + "ws/topics");

    webSocket.onmessage = function (message) {
        var data = JSON.parse(message.data);

        if (data.cluster === topicService.getCluster()) {
            var currentTopics = {};

            topicService.getKeys().forEach(function (topicName) {
                currentTopics[topicName] = false;
            });

            var newTopics = [];

            data.topics.sort().forEach(function (topicName) {
                if (!currentTopics.hasOwnProperty(topicName)) {
                    newTopics.push(topicName);
                }
                currentTopics[topicName] = true;
            });

            for (var topicName in currentTopics) {
                if (currentTopics[topicName] === false) {
                    topicService.remove(topicName);
                }
            }

            if (newTopics.length > 0) {
                $http
                        .get($rootScope.appContext + "clusters/" + data.cluster + "/topics?names=" + newTopics.join(","))
                        .then(function (response) {
                            if (response.status === 200) {
                                topicService.add(response.data, true, true, PaginationService);
                                topicService.callCallbacks();
                            }
                        });
            } else {
                topicService.callCallbacks();
            }
        }
    };

    return topicService;
});