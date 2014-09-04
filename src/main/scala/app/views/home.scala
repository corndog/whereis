package whereis.views

object Home {
	val homePage =
		<html>
			<head>
				<title>Where are you?</title>
				<meta name="viewport" content="initial-scale=1.0, user-scalable=no"></meta>
				<meta charset="utf-8"></meta>
				<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&amp;sensor=false"></script>
				<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
				<script src="public/javascript/app.js"></script>
        <link href="/public/css/reset.css" media="all" rel="stylesheet" type="text/css" />
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