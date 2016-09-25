var sampleRequest = {
    "reqtype": "barcreq",
    "transaction-type": "reqfunds",
    "amount": 123,
    "reason": "SOME REASON"
}

/** This is a sample code for your bot**/
function MessageHandler(context, event) {

    var msg = event.message;
    console.log(event);
    console.log("####");
    if ('ibc' == event.contextobj.channeltype) {
        return handleIBCResponse(context, event);
    }
    var dbkey = event.senderobj.channeltype + ":" + event.senderobj.channelid;
    var comps = msg.split(/\s+/g);
    console.log(comps);
    if ('listall' == comps[0].toLowerCase()) {
        context.simpledb.doGet(dbkey, function(c, e) {
            var transactions = JSON.parse(e.dbval || '{}');
            var res = 'Userbot | tid | amount | status\n';

            Object.keys(transactions).forEach(function(key) {
                var val = transactions[key];
                console.log(JSON.stringify(val));
                res = res + val.receiverBot + " | " + key + " | " + val.transRequest.amount + " | " + val.status + "\n";
            });
            // res = res.replace(/{/g, "\n\t").replace(/}/g, "").replace(/"/g, "");
            return context.sendResponse(res);
        });
    } else if ('listalldetailed' == comps[0].toLowerCase()) {
        context.simpledb.doGet(dbkey, function(c, e) {
            var transactions = JSON.parse(e.dbval || '{}');
            return context.sendResponse(JSON.stringify(transactions));
        });
    } else if ('receive' == comps[0].toLowerCase()) {
        if (comps.length != 4 || !comps[1] || !comps[3] || comps[2] != 'from') {
            return context.sendResponse("Invalid msg format.\nSample format : receive <amount> from <phone-no>");
        }
        var phone = comps[3];
        var amount = comps[1];
        try {
            amount = parseInt(amount);
            if (amount < 0) {
                return context.sendResponse("Amount shount be greater than Zero (0)");
            }
        } catch (e) {
            return context.sendResponse("Please enter valid value for amount");
        }
        var userRequest = {
            "reqtype": "barcreq",
            "transaction-type": "reqfunds",
            "amount": amount,
            "reason": "transfer"
        }
        userRequest.tid = "" + Math.round(new Date().getTime() % 100000000);

        var currentTransaction = {
            transRequest: userRequest,
            intiatedFrom: dbkey,
            status: "waiting_for_approval",
            startDate: new Date(),
            userContext: event.contextobj
        }

        // Get receiverBot from registry
        var receiverBot = "simple9819014845";
        var registryUrl = "https://c3f3c77d.ngrok.io/BotRegistrar/getbot?";

        var query = "mobile=" + phone.replace("+91", "") + "&type=wallet";
        context.simplehttp.makeGet(registryUrl + query, null, function(c, e) {
            if (e.getresp) {
                receiverBot = e.getresp;
            }
            currentTransaction.receiverBot = receiverBot;
            var options = {
                context: context,
                event: event,
                senderBot: "merchant4051694345404903",
                receiverBot: receiverBot,
                message: userRequest,
                userContext: event.contextobj,
                refid: dbkey
            }
            sendIBCMessage(options);

            context.simpledb.doGet(dbkey, function(_context, _event) {
                var transactions = JSON.parse(_event.dbval || '{}');
                transactions[userRequest.tid] = currentTransaction;
                sendResponseToUser(context, userRequest.tid, currentTransaction)
                context.simpledb.doPut(dbkey, transactions, function(c, e) {
                    return context.sendResponse("");
                });
            });

        });

    } else {
        context.sendResponse("Invalid msg format.\nSample format : receive <amount> from <phone-no>");
    }
}

/** Functions declared below are required **/
function EventHandler(context, event) {
    if (!context.simpledb.botleveldata.numinstance)
        context.simpledb.botleveldata.numinstance = 0;
    numinstances = parseInt(context.simpledb.botleveldata.numinstance) + 1;
    context.simpledb.botleveldata.numinstance = numinstances;
    context.sendResponse("This is Merchant bot. \nCreated for Rise Hackathon.");
}

function HttpResponseHandler(context, event) {
    // if(event.geturl === "http://ip-api.com/json")
    context.sendResponse(event.getresp);
}

function DbGetHandler(context, event) {
    context.sendResponse("testdbput keyword was last get by:" + event.dbval);
}

function DbPutHandler(context, event) {
    context.sendResponse("testdbput keyword was last put by:" + event.dbval);
}

function LocationHandler(context, event) {
    MessageHandler(context, event);
}

function HttpEndpointHandler(context, event) {
    context.sendResponse("MainBot : " + JSON.stringify(event));
}

function sendIBCMessage(options) {
    var ibcJSON = {
        "refid": options.refid,
        "botname": options.senderBot,
        "channeltype": "ibc",
        "contextid": sortBotsByName(options.senderBot + "-" + options.receiverBot),
        "contexttype": "p2p"
    }

    var mesgJson = {
        "text": options.message,
        "context": options.userContext
    }

    sendMesgApi(options.context, ibcJSON, options.message);
}

function sendMesgApi(context, userContext, message) {
    // context, event, senderBot, receiverBot, message
    var botName = 'merchant4051694345404903';
    var url = 'https://dev-api.gupshup.io/sm/api/bot/' + botName + '/msg';
    var headers = {
        'content-type': 'application/x-www-form-urlencoded',
        'apikey': '2d045dc742134d98cd475cd63196c6c1'
    }
    console.log('message :-> ' + JSON.stringify(message));
    var msgStr = (isJson(message)) ? JSON.stringify(message) : message;
    console.log('msgStr :-> ' + msgStr);
    var formData = 'context=' + JSON.stringify(userContext) + '&message=' + msgStr;
    console.log('sendMesgApi :->' + formData);
    context.simplehttp.makePost(url, formData, headers, function(context_, event_) {
        context.console.log("Sent mesg on IBC channel : " + JSON.stringify(event_));
    });
}

function handleIBCResponse(context, event) {

    var messageObj = event.messageobj.text;
    var message;
    if (isJson(messageObj)) {
        messageObj = JSON.parse(messageObj);
        message = messageObj;
    } else {
        message = messageObj;
    }
    var dbkey = event.contextobj.refid;

    context.simpledb.doGet(dbkey, function(_context, _event) {
        if (!_event.dbval) {
            return context.sendResponse("No record found for transaction id : " + message.tid);
        }
        var transactions = JSON.parse(_event.dbval);
        if (transactions[message.tid]) {
            var existingTransaction = transactions[message.tid];
            if (message.payload) existingTransaction.result = message.payload;
            if (message.status) existingTransaction.status = message.status;
            if (message.reason) existingTransaction.reason = message.reason;
            existingTransaction.lastUpdateOn = new Date();
            transactions[message.tid] = existingTransaction;
            context.simpledb.doPut(dbkey, transactions, function(c, e) {
                return context.sendResponse("");
            });

            sendResponseToUser(context, message.tid, existingTransaction);

        } else {
            return context.sendResponse("No record found for transaction id : " + message.tid);
        }
    });



    // Sample response
    // {"payload":"Dummy Approved","reason":"approved",
    // "reqtype":"barcreq","status":"success","tid":"1","transaction-type":"reqfunds-response"}

    // roomleveldata will be pre loaded by botrunner...so no need to get data from DB.
    // message = 'FinalResponse:->' + message;
    // sendMesgApi(context, userContext, message);
}

function sendResponseToUser(context, tid, userTransaction) {
    var message = "Transaction details : \nUser bot :" + userTransaction.receiverBot + "\nid : " + tid + "\nStatus : " + userTransaction.status;
    if (userTransaction.userContext) {
        console.log('Sending status to Merchat');
        var userContext = userTransaction.userContext;
        sendMesgApi(context, userContext, message);
    }
}



function sortBotsByName(botsStr) {
    var arr = botsStr.split('-');
    arr.sort(function(a, b) {
        return a.toLowerCase().localeCompare(b.toLowerCase());
    });
    return arr.join('-');
}

function isJson(jsonStr) {
    try {
        if (typeof jsonStr == 'string') {
            JSON.parse(jsonStr);
        } else if (typeof jsonStr == 'object') {
            return true;
        }
    } catch (e) {
        return false;
    }
    return true;
}

exports.onMessage = MessageHandler;
exports.onEvent = EventHandler;
exports.onHttpResponse = HttpResponseHandler;
exports.onDbGet = DbGetHandler;
exports.onDbPut = DbPutHandler;
exports.onLocation = LocationHandler;
exports.onHttpEndpoint = HttpEndpointHandler;