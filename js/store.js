function Store() {
	this.save_success = null;
	this.save_error = null;
	this.loadAll_success = null;
	this.loadAll_error = null;
	this.load_success = null;
	this.load_error = null;
};

Store.prototype.getAll = function(successCallback,errorCallback) {
	this.loadAll_success = successCallback;
	this.loadAll_error = errorCallback;
	device.exec("store",["loadAll"]);
}

Store.prototype.put = function(successCallback,errorCallback,key,data) {
	this.save_success = successCallback;
	this.save_error = errorCallback;
	device.exec("store",["save",key,data]);
}

Store.prototype.get = function(successCallback,errorCallback,key) {
	this.load_success = successCallback;
	this.load_error = errorCallback;
	device.exec("store",["load",key]);
}

if (typeof navigator.store == "undefined") navigator.store = new Store();