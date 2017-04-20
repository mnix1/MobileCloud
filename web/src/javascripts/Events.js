Events = {
  listener: {},
  addListener: function (type, listenerId, func) {
    var typeListener = Events.listener[type];
    if (!typeListener) {
      Events.listener[type] = {listenerId: func};
    } else {
      typeListener[listenerId] = func;
    }
  },
  removeListener: function (type, listenerId) {
    var typeListener = Events.listener[type];
    if (typeListener) {
      delete typeListener[listenerId];
    }
  },
  invoke: function (type, data, ctx) {
    var typeListener = Events.listener[type];
    if (typeListener) {
      for (var key in typeListener) {
        typeListener[key].call(ctx || this, data);
      }
    }
  }
}
Events.type = {
};