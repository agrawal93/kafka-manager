var app = angular.module("kafka-manager");

app.service("BrokerService", function ($rootScope, $http, PaginationService) {
    var brokerService = new BaseService();

    brokerService.get = function (id) {
        return this.object[id];
    };

    brokerService.init = function (clusterId) {
        return $http
                .get($rootScope.appContext + "clusters/" + clusterId + "/brokers")
                .then(function (response) {
                    if (response.status === 200) {
                        brokerService.reset();
                        brokerService.setCluster(clusterId);
                        brokerService.setData(response.data, true, true, PaginationService);
                    }
                });
    };

    var webSocket = new WebSocket("ws://" + window.location.host + $rootScope.appContext + "ws/brokers");

    webSocket.onmessage = function (message) {
        var data = JSON.parse(message.data);

        if (data.cluster === brokerService.getCluster()) {
            var currentBrokers = {};

            brokerService.getKeys().forEach(function (brokerId) {
                currentBrokers[brokerId] = false;
            });

            var newBrokers = [];

            data.brokers.sort().forEach(function (brokerId) {
                if (!currentBrokers.hasOwnProperty(brokerId)) {
                    newBrokers.push(brokerId);
                }
                currentBrokers[brokerId] = true;
            });

            for (var brokerId in currentBrokers) {
                if (currentBrokers[brokerId] === false) {
                    brokerService.remove(brokerId);
                }
            }

            if (newBrokers.length > 0) {
                $http
                        .get($rootScope.appContext + "clusters/" + data.cluster + "/brokers?ids=" + newBrokers.join(","))
                        .then(function (response) {
                            if (response.status === 200) {
                                brokerService.add(response.data, true, true, PaginationService);
                                brokerService.callCallbacks();
                            }
                        });
            } else {
                brokerService.callCallbacks();
            }
        }
    };

    return brokerService;
});