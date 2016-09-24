var sampleRequest = {
    "reqtype": "barcreq",
    "transaction-type": "reqfunds",
    "amount": 123,
    "reason": "SOME REASON"
}

/** This is a sample code for your bot**/
function MessageHandler(context, event) {

    if (!context.simpledb.botleveldata.transactions) {
        context.simpledb.botleveldata.transactions = [];
    }

    var msg = event.message;
    console.log(event);
    console.log("####");
    if ('ibc' == event.contextobj.channeltype) {
        return handleIBCResponse(context, event);
    }

    var userRequest = {};
    try {
        userRequest = JSON.parse(msg);
        userRequest.tid = "" + new Date().getTime();
    } catch (e) {
        if ('listall' == msg.toLowerCase()) {
            return context.sendResponse(JSON.stringify(context.simpledb.botleveldata.transactions));
        }
        return context.sendResponse("Invalid msg format : Please check the sample msg format : \n" + JSON.stringify(sampleRequest));
    }

    var currentTransaction = {
        transRequest: userRequest,
        intiatedFrom: event.senderobj.channeltype + ":" + event.senderobj.channelid,
        status: "waiting_for_approval",
        startDate: new Date()
    }

    context.simpledb.botleveldata.transactions[userRequest.tid] = currentTransaction;

    var options = {
        context: context,
        event: event,
        senderBot: "MerchantIBC",
        receiverBot: "bdemo1",
        message: userRequest,
        userContext: event.contextobj,
        refid: event.senderobj.channeltype + ":" + event.senderobj.channelid
    }
    sendIBCMessage(options);
    context.sendResponse("Initiated request : " + JSON.stringify(userRequest));
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
    var botName = 'MerchantIBC';
    var url = 'https://dev-api.gupshup.io/sm/api/bot/' + botName + '/msg';
    var headers = {
        'content-type': 'application/x-www-form-urlencoded',
        'apikey': 'd5a5da48c1fe45f1c52dd1fdaf4da8a4'
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

    if (context.simpledb.botleveldata.transactions[message.tid]) {
        var existingTransaction = context.simpledb.botleveldata.transactions[message.tid];
        if (message.payload) existingTransaction.result = message.payload;
        if (message.status) existingTransaction.status = message.status;
        if (message.reason) existingTransaction.reason = message.reason;
        existingTransaction.lastUpdateOn = new Date();
        context.simpledb.botleveldata.transactions[message.tid] = existingTransaction;
        return context.sendResponse("");
    } else {
        return context.sendResponse("No record found for transaction id : " + message.tid);
    }

    // Sample response
    // {"payload":"Dummy Approved","reason":"approved",
    // "reqtype":"barcreq","status":"success","tid":"1","transaction-type":"reqfunds-response"}

    // roomleveldata will be pre loaded by botrunner...so no need to get data from DB.
    // message = 'FinalResponse:->' + message;
    // sendMesgApi(context, userContext, message);
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
