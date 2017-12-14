function BaseService() {
    this.currentCluster = null;
    this.keys = null;
    this.object = {};
    this.pages = {
        selectedPage: 1,
        recordCount: 0,
        pageCount: 0,
        pages: [],
        registeredService: null,
        getPage: null
    };
    this.observerCallbacks = [];
}

BaseService.prototype.setCluster = function (clusterId) {
    this.currentCluster = clusterId;
};

BaseService.prototype.getCluster = function () {
    return this.currentCluster;
};

BaseService.prototype.count = function () {
    return this.keys.length;
};

BaseService.prototype.getKeys = function () {
    return this.keys;
};

BaseService.prototype.getAll = function () {
    return this.object;
};

BaseService.prototype.get = function (id) {
    if (typeof id === "number") {
        return Object.assign({}, this.object[this.keys[id]]);
    } else {
        return Object.assign({}, this.object[id]);
    }
};

BaseService.prototype.getPages = function () {
    return this.pages;
};

BaseService.prototype.reset = function () {
    delete this.keys;
    for (var key in this.object) {
        delete this.object[key];
    }
};

BaseService.prototype.setData = function (data, sorted, paged, PaginationService) {
    this.keys = Object.keys(data);
    if (sorted) {
        this.keys = this.keys.sort();
    }

    for (var key of this.keys) {
        this.object[key] = data[key];
    }

    if (paged) {
        _paginate(PaginationService, this, this.pages);
    }
};

BaseService.prototype.add = function (data, sorted, paged, PaginationService) {
    if (sorted) {
        var sortedKeys = [];
        var originalKeys = this.keys.slice();
        var dataKeys = Object.keys(data).sort();

        var i = 0, j = 0;
        while (i < originalKeys.length && j < dataKeys.length) {
            if (originalKeys[i] < dataKeys[j]) {
                sortedKeys.push(originalKeys[i++]);
            } else {
                sortedKeys.push(dataKeys[j++]);
            }
        }

        while (i < originalKeys.length) {
            sortedKeys.push(originalKeys[i++]);
        }

        while (j < dataKeys.length) {
            sortedKeys.push(dataKeys[j++]);
        }

        this.keys = sortedKeys;
    } else {
        Object.keys(data).forEach(function (key) {
            this.keys.push(key);
        });
    }

    for (var key of dataKeys) {
        this.object[key] = data[key];
    }

    if (paged) {
        _paginate(PaginationService, this, this.pages);
    }
};

BaseService.prototype.remove = function (id) {
    this.keys.splice(this.keys.indexOf(id), 1);
    delete this.object[id];
};

BaseService.prototype.registerCallback = function (callback) {
    if (callback && typeof callback === "function") {
        this.observerCallbacks.push(callback);
    }
};

BaseService.prototype.callCallbacks = function () {
    for (var callback of this.observerCallbacks) {
        callback();
    }
};

function _paginate(PaginationService, BaseService, pages) {
    var paginationResult = PaginationService.paginate(BaseService);

    pages.selectedPage = paginationResult.selectedPage;
    pages.recordCount = paginationResult.recordCount;
    pages.pageCount = paginationResult.pageCount;
    pages.pages = paginationResult.pages;
    pages.registeredService = paginationResult.registeredService;
    pages.getPage = paginationResult.getPage;
}