// another experiment
package com.whereis.pages

import com.whereis.js.Scripts

object Pages { 
	val styles = """ 
		html, body, #map-canvas {
			margin: 0;
			padding: 0;
			height: 100%;
	} """

	val homePage =
		<html>
			<head>
				<title>Where are you?</title>
				<meta name="viewport" content="initial-scale=1.0, user-scalable=no"></meta>
				<meta charset="utf-8"></meta>
				<style>{xml.Text(styles)}</style>
				<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&amp;sensor=false"></script>
				<script>{xml.Unparsed(Scripts.microAjax)}</script>
				<script>{xml.Unparsed(Scripts.initMap)}</script>	
			</head>
			<body>
				<div>
					<span>Start transmitting!</span>
					<span><button type="button" onclick="start();">START!</button></span>
					<span><button type="button" onclick="stop();">STOP!</button></span>
				</div>
				<div id="map-canvas"></div>
			</body>
		</html>
}