var jwt = "JWT";

function getJwt() {
	var username = document.getElementById('username').value;
	var password = document.getElementById('password').value;

	var credentials = {};
	credentials.username = username;
	credentials.password = password;
	var payload = JSON.stringify(credentials);

	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/api/login", true);
	xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');


	xhr.onload = function() {
		var response = xhr.responseText;
		if (xhr.readyState === 4 && xhr.status === 200) {
			console.log(response);
			jwt = response;
		} else {
			console.error(response);
		}
	}

	xhr.send(payload);

}
