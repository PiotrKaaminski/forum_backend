var stompClient = null;

function connect() {
	var socket = new SockJS('/my-socket-endpoint');
	stompClient = Stomp.over(socket);
	stompClient.connect({"Authorization": "Bearer " + jwt}, function(frame) {

		setConnected(true);
		console.log('Connected: ' + frame);

		stompClient.subscribe('/topic/chat', function (response) {
			console.log(response.body);
			addMessage(JSON.parse(response.body));
		});

		stompClient.subscribe('/user/queue/whisper', function (response) {
			console.log(response);
			addWhisper(JSON.parse(response.body));
		})

	})
}

function disconnect() {
	if (stompClient !== null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function setConnected(connected) {
	document.getElementById('connectButton').disabled = connected;
	document.getElementById('disconnectButton').disabled = !connected;
}

function sendMessage() {
	var message = document.getElementById('message').value;
	stompClient.send("/app/chat", {}, message);
}

function addMessage(message) {
	let trNode = document.createElement("tr");
	let tdNode1 = document.createElement("td");
	let tdNode2 = document.createElement("td");
	let bNode = document.createElement("b");
	bNode.appendChild(document.createTextNode(message.username + ": "));
	tdNode1.appendChild(bNode);
	tdNode2.appendChild(document.createTextNode('Message: ' + message.message));
	trNode.appendChild(tdNode1);
	trNode.appendChild(tdNode2);
	document.getElementById('messages').appendChild(trNode);
}

function addWhisper(whisper) {
	let trNode = document.createElement("tr");
	let tdNode1 = document.createElement("td");
	let tdNode2 = document.createElement("td");
	let bNode = document.createElement("b");
	bNode.appendChild(document.createTextNode(whisper.username));
	tdNode1.appendChild(bNode);
	tdNode2.appendChild(document.createTextNode(whisper.message));
	trNode.appendChild(tdNode1);
	trNode.appendChild(tdNode2);
	document.getElementById('whispers').appendChild(trNode);
}

