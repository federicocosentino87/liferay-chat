let client;
let handlers = {};
const ENDPOINT = "ws://localhost:8080/o/chat";

export const initWs = (ctxt) => {

	client = new WebSocket(ENDPOINT);

	client.onopen = function() {
		//this.send('{msgType: "ACTIVE_USER", userId: "' + ctxt.userId + '"}');
		console.log("CONNECTED");
	};

	client.onclose = function(evt) {
		console.log("DISCONNECTED");
	};
	
	client.onmessage = function(event) {

		let message = JSON.parse(event.data);
		//console.log(message);

		handle(message);
	}

	return client;
}

function handle(message) {

	let msgType = message.msgType;

	if (handlers.hasOwnProperty(msgType)) {

		handlers[msgType](message);
	}
	else {
		console.log("No handlers found for message type: " + msgType);
	}
}

export const setWsHandler = (key, handler) => {
	handlers[key] = handler;
}

export const getWs = () => {
	return client;
}

export const sendWsMessage = (message) => {
	getWs().send(message);
}
