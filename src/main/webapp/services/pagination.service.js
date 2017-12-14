var app = angular.module("kafka-manager");

app.service("PaginationService", function () {

    var paginate = function (service, limit) {

        if (!limit)
            limit = 15; // 15 records per page

        var recordCount = service.count();

        var result = [];
        var page = 0;
        while (recordCount > 0) {
            result.push({
                start: page++ * limit + 1,
                count: Math.min(recordCount, limit)
            });
            recordCount -= limit;
        }

        return {
            selectedPage: 1,
            recordCount: service.count(),
            pageCount: result.length,
            pages: result,
            registeredService: service,
            getPage: function (pageNo) {
                var page = this.pages[pageNo];
                var object = [];
                var start = page.start;
                for (var i = 0; i < page.count; i++) {
                    var record = this.registeredService.get(start++);
                    if (Object.keys(record).length > 0)
                        object.push(record);
                }
                return object;
            }
        };
    };

    return {
        paginate: paginate
    };

});
