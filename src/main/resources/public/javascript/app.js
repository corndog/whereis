(function() {

	var shouldTransmit = false, transmitId, receiveId;
	var transmitFreq = 5000, receiveFreq = 5000; // milliseconds
	var markers = [], routeMarkers = [], map;
		
		function clearMarkers(container) {
			var i;
			for (i = 0; i< container.length; i++ ) {
				container[i].setMap(null);
				container[i] = null;
			}
			container.length = 0; // empty it 
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
      google.maps.event.addListener(map, 'dragend', handleScroll);
			//receive();
		}

		function handleScroll() { /// argh maps calls lon lng !!
			var center = map.getCenter();
			fetchItems( { coords: { latitude: center.lat(), longitude: center.lng() }  } , 'stops');
		}
		
		function findLocation() {
			navigator.geolocation.getCurrentPosition(initialize, noLocation);
		}
		
    // we have pointType but will worry about that later
		function displayPoints(data) {
			console.log(data);
			clearMarkers(markers);
			var points = data.points, i, marker, point, id, lat, lon;
			for (i = 0; i < points.length; i++) {
				point = points[i];
				marker = new google.maps.Marker({
					position:  new google.maps.LatLng(parseFloat(point.lat), parseFloat(point.lon)),
					map: map,
					title: point.mref
				});
				google.maps.event.addListener(marker, 'click', stopClickHandler(point.mref));
				markers.push(marker);
			}
		}

		function addPointsForRoutes(data) {
			var points = data.points, i, marker, point, id, lat, lon;
			clearMarkers(routeMarkers);
			for (i = 0; i < points.length; i++) {
				point = points[i];
				marker = new google.maps.Marker({
					position:  new google.maps.LatLng(parseFloat(point.lat), parseFloat(point.lon)),
					map: map,
					icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
					title: point.mref
				});
				routeMarkers.push(marker);
			}
		}

		function stopClickHandler(stopCode) {
			return function() {
				getRoutesForStop(stopCode);
			};
		}
		
		function send(position) {
			var lat = position.coords.latitude;
			var lon = position.coords.longitude;
			var data = 'lat=' + plat + '&lon=' + lon;
			//console.log('send ' + body);
			$.post('/spot', data).done(function(resp){ console.log(resp);}); 
		}
		
		function fetchItems(position, pointType) {
			var lat = encodeURIComponent(position.coords.latitude);
			var lon = encodeURIComponent(position.coords.longitude);
			var url = '/' + pointType + '?lat=' + lat + '&lon=' + lon;
			$.get(url).done(displayPoints);
		}

		function getRoutesForStop(stopCode) {
			var url ='/routes/' + stopCode; 
			$.get(url).done(addPointsForRoutes);
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