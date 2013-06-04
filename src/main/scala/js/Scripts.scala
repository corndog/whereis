// a crazy experiment, who needs separate js files LOL
package com.whereis.js

object Scripts {

	val initMap = """
		var intervalID;
		var transmitting = false;
		var transmitFreq = 5000; // milliseconds
		var map;
		function noLocation() { alert('OH SHIT'); }
		function initialize(position) {
			var lat = position.coords.latitude;
			var lon = position.coords.longitude;
			console.log(' LAT, LONG ' + lat + ' : ' + lon);
			var mapOptions = {
				zoom: 16,
				center: new google.maps.LatLng(lat, lon),
				mapTypeId: google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
			google.maps.event.addListener(map, 'click', function(event) {
				var position = event.latLng;
				//map.setCenter(position);
			
				var lat = position.jb;
				var lon = position.kb;
				console.log(' LAT, LONG ' + lat + ' : ' + lon);
			});
		}
		function findLocation() {
			navigator.geolocation.getCurrentPosition(initialize, noLocation);
		}
		google.maps.event.addDomListener(window, 'load', findLocation);
		
		function send(position) {
			var lat = position.coords.latitude;
			var lon = position.coords.longitude;
			var coords = lat + 'S' + lon;
			var body = 'spot=' + encodeURIComponent(coords);
			console.log('send ' + body);
			microAjax('/spot', function(resp){console.log(resp)}, body);
		}
		
		function transmit() {
			// assume we are moving ;)
			navigator.geolocation.getCurrentPosition(send, noLocation);
		}
		
		function stop() { clearInterval(intervalID); }
		
		function start() { 
			intervalID = window.setInterval(transmit, transmitFreq);
		}
	"""

	
	val microAjax = """
	function microAjax(url, callbackFunction) {
		this.bindFunction = function (caller, object) {
			return function() {
				return caller.apply(object, [object]);
			};
		};

		this.stateChange = function (object) {
			if (this.request.readyState==4)
				this.callbackFunction(this.request.responseText);
		};

		this.getRequest = function() {
			if (window.ActiveXObject)
				return new ActiveXObject('Microsoft.XMLHTTP');
			else if (window.XMLHttpRequest)
				return new XMLHttpRequest();
			return false;
		};

		this.postBody = (arguments[2] || '');

		this.callbackFunction=callbackFunction;
		this.url=url;
		this.request = this.getRequest();
	
		if(this.request) {
			var req = this.request;
			req.onreadystatechange = this.bindFunction(this.stateChange, this);

			if (this.postBody!=='') {
				req.open('POST', url, true);
				req.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
				req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
				//req.setRequestHeader('Connection', 'close');
			} else {
			req.open('GET', url, true);
			}

			req.send(this.postBody);
		}
	}
	"""
}