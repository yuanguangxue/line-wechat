
                            case "RECEIPT":
                                _results.push(typeof _this.onreceipt === "function" ? _this.onreceipt(frame) : void 0);
                                break;
                            case "ERROR":
                                _results.push(typeof errorCallback === "function" ? errorCallback(frame) : void 0);
                                break;
                            default:
                                _results.push(typeof _this.debug === "function" ? _this.debug("Unhandled frame: " + frame) : void 0);
                        }
                    }
                    return _results;
                };
            })(this);
            this.ws.onclose = (function(_this) {
                return function() {
                    var msg;
                    msg = "Whoops! Lost connection to " + _this.ws.url;
                    if (typeof _this.debug === "function") {
                        _this.debug(msg);
                    }
                    _this._cleanUp();
                    return typeof errorCallback === "function" ? errorCallback(msg) : void 0;
                };
            })(this);
            return this.ws.onopen = (function(_this) {
                return function() {
                    if (typeof _this.debug === "function") {
                        _this.debug('Web Socket Opened...');
                    }
                    headers["accept-version"] = Stomp.VERSIONS.supportedVersions();
                    headers["heart-beat"] = [_this.heartbeat.outgoing, _this.heartbeat.incoming].join(',');
                    return _this._transmit("CONNECT", headers);
                };
            })(this);
        };

        Client.prototype.disconnect = function(disconnectCallback, headers) {
            if (headers == null) {
                headers = {};
            }
            this._transmit("DISCONNECT", headers);
            this.ws.onclose = null;
            this.ws.close();
            this._cleanUp();
            return typeof disconnectCallback === "function" ? disconnectCallback() : void 0;
        };

        Client.prototype._cleanUp = function() {
            this.connected = false;
            if (this.pinger) {
                Stomp.clearInterval(this.pinger);
            }
            if (this.ponger) {
                return Stomp.clearInterval(this.ponger);
            }
        };

        Client.prototype.send = function(destination, headers, body) {
            if (headers == null) {
                headers = {};
            }
            if (body == null) {
                body = '';
            }
            headers.destination = destination;
            return this._transmit("SEND", headers, body);
        };

        Client.prototype.subscribe = function(destination, callback, headers) {
            var client;
            if (headers == null) {
                headers = {};
            }
            if (!headers.id) {
                headers.id = "sub-" + this.counter++;
            }
            headers.destination = destination;
            this.subscriptions[headers.id] = callback;
            this._transmit("SUBSCRIBE", headers);
            client = this;
            return {
                id: headers.id,
                unsubscribe: function() {
                    return client.unsubscribe(headers.id);
                }
            };
        };

        Client.prototype.unsubscribe = function(id) {
            delete this.subscriptions[id];
            return this._transmit("UNSUBSCRIBE", {
                id: id
            });
        };

        Client.prototype.begin = function(transaction) {
            var client, txid;
            txid = transaction || "tx-" + this.counter++;
            this._transmit("BEGIN", {
                transaction: txid
            });
            client = this;
            return {
                id: txid,
                commit: function() {
                    return client.commit(txid);
                },
                abort: function() {
                    return client.abort(txid);
                }
            };
        };

        Client.prototype.commit = function(transaction) {
            return this._transmit("COMMIT", {
                transaction: transaction
            });
        };

        Client.prototype.abort = function(transaction) {
            return this._transmit("ABORT", {
                transaction: transaction
            });
        };

        Client.prototype.ack = function(messageID, subscription, headers) {
            if (headers == null) {
                headers = {};
            }
            headers["message-id"] = messageID;
            headers.subscription = subscription;
            return this._transmit("ACK", headers);
        };

        Client.prototype.nack = function(messageID, subscription, headers) {
            if (headers == null) {
                headers = {};
            }
            headers["message-id"] = messageID;
            headers.subscription = subscription;
            return this._transmit("NACK", headers);
        };

        return Client;

    })();

    Stomp = {
        VERSIONS: {
            V1_0: '1.0',
            V1_1: '1.1',
            V1_2: '1.2',
            supportedVersions: function() {
                return '1.1,1.0';
            }
        },
        client: function(url, protocols) {
            var klass, ws;
            if (protocols == null) {
                protocols = ['v10.stomp', 'v11.stomp'];
            }
            klass = Stomp.WebSocketClass || WebSocket;
            ws = new klass(url, protocols);
            return new Client(ws);
        },
        over: function(ws) {
            return new Client(ws);
        },
        Frame: Frame
    };

    if (typeof exports !== "undefined" && exports !== null) {
        exports.Stomp = Stomp;
    }

    if (typeof window !== "undefined" && window !== null) {
        Stomp.setInterval = function(interval, f) {
            return window.setInterval(f, interval);
        };
        Stomp.clearInterval = function(id) {
            return window.clearInterval(id);
        };
        window.Stomp = Stomp;
    } else if (!exports) {
        self.Stomp = Stomp;
    }

}).call(this);