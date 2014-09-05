package whereis.views

object Home {
	val homePage =
		<html>
			<head>
				<title>Where are you?</title>
				<meta name="viewport" content="initial-scale=1.0, user-scalable=no"></meta>
				<meta charset="utf-8"></meta>
				<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
				<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA4DZD5oVZNL_NSZ4ux_aPRH-Ggz3-HFDU"></script>
				<script src="public/javascript/app.js"></script>
        <link href="/public/css/reset.css" media="all" rel="stylesheet" type="text/css" />
				<style type="text/css">
					{xml.Unparsed("""
						html { height: 100% }
						body { height: 100%; margin: 0; padding: 0 }
						#map-canvas { height: 100% }
					""")
					}
				</style>
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