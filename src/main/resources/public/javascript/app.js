(function() {

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
				zoom: 13,
				center: new google.maps.LatLng(lat, lon),
				//mapTypeId: google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
      fetchItems(position, 'stops');
			//receive();
		}
		
		function findLocation() {
			navigator.geolocation.getCurrentPosition(initialize, noLocation);
		}
		
    // we have pointType but will worry about that later
		function displayPoints(data) {
			console.log(data);
			clearMarkers();
			var points = data.points, i, marker, point, id, lat, lon;
			for (i = 0; i < points.length; i++) {
				point = points[i];
				marker = new google.maps.Marker({
					position:  new google.maps.LatLng(parseFloat(point.lat), parseFloat(point.lon)),
					map: map,
					title: point.mref
				});
				markers.push(marker);
			}
		}
		
		function send(position) {
			var lat = position.coords.latitude;
			var lon = position.coords.longitude;
			var data = 'lat=' + lat + '&lon=' + lon;
			console.log('send ' + body);
			$.post('/spot', data).done(function(resp){ console.log(resp);}); 
		}
		
		function fetchItems(position, pointType) {
			var lat = encodeURIComponent(position.coords.latitude);
			var lon = encodeURIComponent(position.coords.longitude);
			var url = '/' + pointType + '?lat=' + lat + '&lon=' + lon;
			$.get(url).done(displayPoints);
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
}());