var http = require('http');
var bot = require('./index');
var url = require('url');
var http = require('http');
var https = require('https');
var async = require('async');
var url = require('url');

var botenv = {};
botenv.botcontext = {}

function setupContext() {
    setupHttp();
    botenv.botcontext.async = require('async');
    return botenv;
}

function setupHttp() {
    botenv.botcontext.http = require('http');
    botenv.botcontext.https = require('https');
    var simplehttp = {};

    botenv.botcontext.simplehttp = simplehttp;

    simplehttp.parseURL = function parseURL(href) {
        return url.parse(href);
    };

    simplehttp.makeGet = function makeGet(geturl, headers, callback) {
        var options = simplehttp.parseURL(geturl);
        var headerJson = {};
        if (headers) {
            headerJson = headers;
        }
        options.headers = headerJson;
        simplehttp.httpRequest(options, callback);
    };

    simplehttp.makePost = function makePost(geturl, formParams, headers, callback) {
        var options = simplehttp.parseURL(geturl);
        var headerJson = {
            'Content-Type': 'application/x-www-form-urlencoded'
        };
        if (headers) {
            headerJson = headers;
        }
        options.headers = headerJson;
        options.method = 'POST';
        options.body = formParams;
        simplehttp.httpRequest(options, callback);
    };

    simplehttp.httpRequest = function httpRequest(options, done) {
        botenv.botcontext.console.log('options for httpRequest : ' + JSON.stringify(options));
        var callback = function(response) {
            var body = '';
            response.on('data', function(d) {
                body += d;
            });
            response.on('end', function() {
                var event1 = botenv.botcontext.createEventCopy();
                event1.type = "httprequest";
                event1.geturl = options.href;
                event1.getresp = body;
                event1.options = options;
                console.log('Response from HttpCall ' + JSON.stringify(event1.getresp));
                if (done) {
                    done(botenv.botcontext, event1);
                } else {
                    bot.onHttpResponse(botenv.botcontext, event1);
                }
            });
            response.on('error', function(e) {
                var event1 = botenv.botcontext.createEventCopy();
                event1.type = "httprequest";
                event1.geturl = options.href;
                event1.getresp = JSON.stringify(e);
                if (done) {
                    done(botenv.botcontext, event1);
                } else {
                    bot.onHttpResponse(botenv.botcontext, event1);
                }
            });
        }
        var request;
        if ('https:' == options.protocol) {
            request = botenv.botcontext.https.request(options, callback)
        } else {
            request = botenv.botcontext.http.request(options, callback)
        }
        if (options.body)
            request.write(options.body);
        request.end();
    };

    botenv.botcontext.simplehttp = simplehttp;
}

function executeFunction(context, event, res) {
    // setupLogs(event.botname);
    setupSimpleDb(context, event);
    setupMisc(context, event, res);
    context.simpledb.fetchCommonAndContinue(function() {
        try {
            if (botenv.isHttpRequest) {
                if (typeof handler.onHttpEndpoint == 'function') {
                    bot.onHttpEndpoint(botcontext, event);
                } else {
                    context.sendError(error, true);
                }
            } else if (event.messageobj.type === "event") {
                bot.onEvent(context, event);
            } else if (event.messageobj.type === "location") {
                if (typeof handler.onLocation == 'function') {
                    bot.onLocation(context, event);
                } else {
                    bot.onMessage(context, event);
                }
            } else {
                bot.onMessage(context, event);
            }
        } catch (e) {
            console.error(e);
            botenv.botcontext.sendError(e);
        }
    });
}


function setupSimpleDb(botcontext, botevent) {
    var configuration = require('config.json')('./config.json');
    var simpledb = {};
    var aws = require('aws-sdk');
    aws.config.maxRetries = 5;
    aws.config.sslEnabled = false;
    var accessKey = configuration["aws_access_key_id"];
    var secretKey = configuration["aws.secretKey"];
    /*aws.config.update({
        accessKeyId: accessKey,
        secretAccessKey: secretKey
    });*/
    /*aws.config.update({
        region: configuration["aws.region"],
        endpoint:configuration["aws.dynamoEndPoint"]
    });*/

    aws.config.loadFromPath("./config.json");
    var db = new aws.DynamoDB.DocumentClient();
    var botkey = "bot:global";
    var roomkey = "";
    if (!botenv.isHttpRequest) {
        if ('ibc' == botevent.contextobj.channeltype) {
            try {
                var messageObj = botevent.messageobj.text;
                messageObj = JSON.parse(messageObj);
                var userContext = messageObj.context;
                // lets load roomleveldata for that user.
                var dbKey = userContext.contextid || "";
                roomkey = "room:" + dbKey;
                console.log('Setting roomKey as ' + roomkey);
            } catch (e) {
                roomkey = "room:" + botevent.contextobj.contextid;
                console.log('Setting roomKey as ' + roomkey);
            }
        } else {
            roomkey = "room:" + botevent.contextobj.contextid;
        }
    }
    var tableName = 'botdata';
    var botname = botevent.botname;
    botenv.botcontext.simpledb = {};
    var fetchCommonAndContinue = function(onContinue) {
        botenv.botcontext.async.parallel({
            botleveldata: function(callback) {
                fetchItem(botkey, callback);
            },
            roomleveldata: function(callback) {
                if (!botenv.isHttpRequest) {
                    fetchItem(roomkey, callback);
                } else {
                    botenv.botcontext.console.log('Dont set roomleveldata for http endpoint request');
                    callback(null, "");
                }
            }
        }, function(error, data) {
            botleveldata = data.botleveldata;
            roomleveldata = data.roomleveldata;
            if (!botleveldata) {
                botleveldata = {};
            } else {
                botleveldata = JSON.parse(botleveldata);
                if (!botleveldata) {
                    botleveldata = {};
                }
            }
            if (!roomleveldata) {
                roomleveldata = {};
            } else {
                roomleveldata = JSON.parse(roomleveldata);
                if (!roomleveldata) {
                    roomleveldata = {};
                }
            }
            botenv.botcontext.simpledb.botleveldata = botleveldata;
            botenv.botcontext.simpledb.roomleveldata = roomleveldata;
            botenv.botcontext.console.log('Setting up botleveldata : ' + JSON.stringify(botleveldata));
            botenv.botcontext.console.log('Setting up roomleveldata : ' + JSON.stringify(roomleveldata));
            onContinue();
        });
    }

    var saveCommonAndContinue = function(onContinue) {
        botenv.botcontext.async.parallel([function(callback) {
            botenv.botcontext.console.log('Trying to save botleveldata : ' + JSON.stringify(simpledb.botleveldata));
            saveItem(botkey, getStringForm(simpledb.botleveldata), callback);
        }, function(callback) {
            if (!botenv.isHttpRequest) {
                botenv.botcontext.console.log('Trying to save roomleveldata : ' + JSON.stringify(simpledb.roomleveldata));
                saveItem(roomkey, getStringForm(simpledb.roomleveldata), callback);
            } else {
                callback(null, {});
            }
        }], function(error, data) {
            if (error) {
                botenv.botcontext.console.log(error);
            }
            onContinue();
        });
    }

    var doGet = function(dbkey, dbCallback) {
        fetchItem(dbkey, function(err, data) {
            var event1 = botenv.botcontext.createEventCopy();
            event1.type = "dbget";
            event1.dbkey = dbkey;

            if (err) {
                event1.result = "failed";
                event1.cause = {};
                event1.cause.msg = "error";
                event1.cause.err = err;
            } else if (typeof data === "undefined") {
                event1.result = "failed";
                event1.cause = {};
                event1.cause.msg = "not found";
            } else {
                botenv.botcontext.console.log('Found Data for Key ' + dbkey + ' :-> ' + data);
                event1.dbval = data;
                event1.result = "success";
            }

            if (dbCallback) {
                dbCallback(botenv.botcontext, event1);
            } else {
                bot.onDbGet(botenv.botcontext, event1);
            }
        });
    };

    var doPut = function doPut(dbkey, dbvalue, dbCallback) {
        saveItem(dbkey, dbvalue, function(err, finalItem) {
            var event1 = botenv.botcontext.createEventCopy();
            event1.type = "dbput";
            event1.dbkey = dbkey;

            if (err) {
                event1.result = "failed";
                event1.cause = {};
                event1.cause.msg = "error";
                event1.cause.err = err;
            } else {
                event1.result = "success";
                event1.dbval = dbvalue;
                event1.finalItem = finalItem;
            }
            if (dbCallback) {
                dbCallback(botenv.botcontext, event1);
            } else {
                bot.onDbPut(botenv.botcontext, event1);
            }
        });
    };

    var saveItem = function(key, value, done) {
        var item_ = {};
        item_['key'] = key;
        item_['botname'] = botname;
        item_['value'] = getStringForm(value);

        var params = {
            TableName: tableName,
            Item: item_
        };
        db.put(params, function(err, data) {
            if (err) {
                botenv.botcontext.console.log(err); // an error occurred
                done(err, null);
            } else {
                botenv.botcontext.console.log('saved to db :->' + JSON.stringify(item_)); // successful
                // response
                done(null, item_);
            }
        });
    };

    var fetchItem = function(key, done) {
        var params = {
            TableName: tableName,
            Key: {
                "botname": botname,
                "key": key
            },
            ConsistentRead: false, // optional (true | false)
            ReturnConsumedCapacity: 'NONE' // optional (NONE | TOTAL | INDEXES)
        }
        botenv.botcontext.console.log('Params to fetch item :-> ' + key);
        db.get(params, function(err, data) {
            if (err) {
                botenv.botcontext.console.log(err);
                done(err, null); // an error occurred
            } else {
                if (data.Item) {
                    var responseData = getStringForm(data.Item.value);
                    done(null, responseData); // successful response
                } else {
                    done(null, "");
                }
            }
        });
    }

    simpledb.fetchCommonAndContinue = fetchCommonAndContinue;
    simpledb.saveCommonAndContinue = saveCommonAndContinue;
    simpledb.doGet = doGet;
    simpledb.doPut = doPut;
    simpledb.saveItem = saveItem;
    simpledb.fetchItem = fetchItem;
    botenv.botcontext.simpledb = {};
    botenv.botcontext.simpledb = simpledb;
}

function isJson(jsonStr) {
    try {
        JSON.parse(jsonStr);
    } catch (e) {
        return false;
    }
    return true;
}

var isEmpty = function(obj) {
    if (typeof obj === "undefined")
        return true;
    return Object.keys(obj).length === 0;
}

var error = {
    code: 404,
    message: "The requested resource was not found"
};

function getStringForm(obj) {
    if (typeof obj === "undefined") {
        return null;
    } else if (typeof obj === "number") {
        return obj + "";
    } else if (typeof obj === "boolean") {
        return obj + "";
    } else if (typeof obj === "string") {
        return obj;
    } else if (typeof obj === "object") {
        return JSON.stringify(obj);
    } else {
        return null;
    }
}


function setupMisc(context, event, res) {
    context.console = {};
    context.console.log = console.log;

    context.createEventCopy = function() {
        var event1 = {};
        Object.keys(event).map(function(key) {
            event1[key] = event[key];
        });
        return event1;
    }

    context.sendResponse = function(respstr) {
        context.simpledb.saveCommonAndContinue(function() {
            context.console.log('Response sent to user :-> ', respstr);
            res.end(respstr);
        });
    }

    context.sendError = function(err, f) {
        context.simpledb.saveCommonAndContinue(function() {
            context.console.log('Sending Error to user :-> ', err);
            if (f) {
                res.end(JSON.stringify(err, null, '\t'));
            } else {
                if (isJson(err)) {
                    res.end('ERROR : ' + JSON.stringify(err, null, '\t'));
                } else {
                    res.end('ERROR : ' + err);
                }
            }
        });
    }
}

function setupLogs(botname) {
    var originalConsoleLog = console.log;
    var originalConsoleError = console.error;
    console.log = function() {
        args = [];
        args.push(' [' + botname + '] | ');
        // Note: arguments is part of the prototype
        for (var i = 0; i < arguments.length; i++) {
            args.push(arguments[i]);
        }
        originalConsoleLog.apply(console, args);
    };
    console.error = function() {
        args = [];
        args.push(' [' + botname + '] | Error : ');
        // Note: arguments is part of the prototype
        for (var i = 0; i < arguments.length; i++) {
            args.push(arguments[i]);
        }
        originalConsoleError.apply(console, args);
    };
}


module.exports = {
    setupContext: setupContext,
    executeFunction: executeFunction,
    setupMisc: setupMisc,
    setupSimpleDb: setupSimpleDb
}
