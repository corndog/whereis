// a crazy experiment, who needs separate js files LOL
package com.whereis.js

object Scripts {

	val initMap = """
		NodeList.prototype.forEach = Array.prototype.forEach;
		var shouldTransmit = false, transmitId, receiveId;
		var transmitFreq = 5000, receiveFreq = 5000; // milliseconds
		var markers = [], map;
		
		function clearMarkers() {
			var i;
			for (i = 0; i< markers.length; i++ ) {
				markers[i].setMap(null);
				markers[i] = null;
			}
			markers = []; 
		}
		function noLocation() { alert('oh, bother'); }
		function initialize(position) {
			var lat = position.coords.latitude;
			var lon = position.coords.longitude;
			console.log(' LAT, LONG ' + lat + ' : ' + lon);
			var mapOptions = {
				zoom: 14,
				center: new google.maps.LatLng(lat, lon),
				mapTypeId: google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
			receive();
			
			// not necessary
			google.maps.event.addListener(map, 'click', function(event) {
				var position = event.latLng;
				var lat = position.jb;
				var lon = position.kb;
				console.log(' LAT, LONG ' + lat + ' : ' + lon);
			});
		}
		function findLocation() {
			navigator.geolocation.getCurrentPosition(initialize, noLocation);
		}
		function handleNearbyItems(respXML) {
			console.log(respXML);
			clearMarkers();
			var points = respXML.getElementsByTagName('item');
			points.forEach(function(it) {
				var id = it.getAttribute('id');
				var lat = it.childNodes[0].firstChild.data;
				var lon = it.childNodes[1].firstChild.data;
				console.log('found point:  ' + id + ' at ' + lat + ' : ' + lon);
				var marker = new google.maps.Marker({
					position:  new google.maps.LatLng(parseFloat(lat), parseFloat(lon)),
					map: map,
					title: id
				});
				markers.push(marker);
			});
		}
		function send(position) {
			var lat = position.coords.latitude;
			var lon = position.coords.longitude;
			var body = 'lat=' + lat + '&lon=' + lon;
			console.log('send ' + body);
			microAjax('/spot', function(respXML){ console.log(respXML); }, body); // POST by inference
		}
		function fetchItems(position) {
			var lat = encodeURIComponent(position.coords.latitude);
			var lon = encodeURIComponent(position.coords.longitude);
			var url = '/spot?lat=' + lat + '&lon=' + lon;
			microAjax(url, handleNearbyItems); // GET
		}
		function receive() { 
			navigator.geolocation.getCurrentPosition(fetchItems, noLocation);
			receiveId = setTimeout(receive, receiveFreq);
		}
		function transmit() {
			if (shouldTransmit) {
				navigator.geolocation.getCurrentPosition(send, noLocation);
				transmitId = setTimeout(transmit, transmitFreq);
			}
		}
		function stop() { 
			shouldTransmit = false;
			clearTimeout(transmitId);  
		}
		function start() {
			shouldTransmit = true;
			transmit();
		}
		google.maps.event.addDomListener(window, 'load', findLocation);
	"""

	// from http://microajax.googlecode.com/svn/trunk/microajax.js
	val microAjax = """
	function microAjax(url, callbackFunction) {
		this.bindFunction = function (caller, object) {
			return function() {
				return caller.apply(object, [object]);
			};
		};

		this.stateChange = function (object) {
			if (this.request.readyState==4)
				this.callbackFunction(this.request.responseXML || this.request.responseText);
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