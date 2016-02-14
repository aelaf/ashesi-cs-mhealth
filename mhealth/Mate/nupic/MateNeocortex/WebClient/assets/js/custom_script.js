$(function(){
	$("#login-form").submit(function(e){
		e.preventDefault();
		login();
	});
});


function login(){
	var username = $("#form-username").val();
	var pass = $("#form-password").val();
	var secret_key = $("#form-key").val();
	alert(username); alert(pass); alert(secret_key);

	if(username == "mate123"){
		if(pass == "mypass123"){
			if(secret_key == "##########"){
				alert("Login was successful");
				window.location.href = "home.html";
			}else{
				return alert("Wrong details");
			}
		}else{
			return alert("Wrong details");
		}
	}else{
		return alert("Wrong details");
	}
}

/* Anomaly chart js */

$(document).ready(function () {
		samplePlot();
		prediction();
	 }
);

function samplePlot(){
	// Some sample data
	var data = "2011-01-01," + Math.random()*100 + "\n" +
		"2011-01-02," + Math.random()*100 + "\n" +
		"2011-01-03," + Math.random()*100 + "\n" +
		"2011-01-04," + Math.random()*100 + "\n" +
		"2011-01-05," + Math.random()*100 + "\n" +
		"2011-01-06," + Math.random()*100 + "\n" +
		"2011-01-07," + Math.random()*100 + "\n" +
		"2011-01-08," + Math.random()*100 + "\n" +
		"2011-01-09," + Math.random()*100 + "\n" +
		"2011-01-10," + Math.random()*100 + "\n" +
		"2011-01-11," + Math.random()*100 + "\n" +
		"2011-01-12," + Math.random()*100 + "\n" +
		"2011-01-13," + Math.random()*100 + "\n" +
		"2011-01-14," + Math.random()*100 + "\n" +
		"2011-01-15," + Math.random()*100 + "\n" +
		"2011-01-16," + Math.random()*100 + "\n" +
		"2011-01-17," + Math.random()*100 + "\n" +
		"2011-01-18," + Math.random()*100 + "\n" +
		"2011-01-19," + Math.random()*100 + "\n" +
		"2011-01-20," + Math.random()*100 + "\n" +
		"2011-01-21," + Math.random()*100 + "\n" +
		"2011-01-22," + Math.random()*100 + "\n" +
		"2011-01-23," + Math.random()*100 + "\n" +
		"2011-01-24," + Math.random()*100 + "\n" +
		"2011-01-25," + Math.random()*100 + "\n" +
		"2011-01-26," + Math.random()*100 + "\n" +
		"2011-01-27," + Math.random()*100 + "\n" +
		"2011-01-28," + Math.random()*100 + "\n" +
		"2011-01-29," + Math.random()*100 + "\n" +
		"2011-01-30," + Math.random()*100 + "\n" +
		"2011-01-31," + Math.random()*100 + "\n" +
		"2011-02-01," + Math.random()*100 + "\n" +
		"2011-02-02," + Math.random()*100 + "\n" +
		"2011-02-03," + Math.random()*100 + "\n" +
		"2011-02-04," + Math.random()*100 + "\n" +
		"2011-02-05," + Math.random()*100 + "\n" +
		"2011-02-06," + Math.random()*100 + "\n" +
		"2011-02-07," + Math.random()*100 + "\n" +
		"2011-02-08," + Math.random()*100 + "\n" +
		"2011-02-09," + Math.random()*100 + "\n" +
		"2011-02-10," + Math.random()*100 + "\n" +
		"2011-02-11," + Math.random()*100 + "\n" +
		"2011-02-12," + Math.random()*100 + "\n" +
		"2011-02-13," + Math.random()*100 + "\n" +
		"2011-02-14," + Math.random()*100 + "\n" +
		"2011-02-15," + Math.random()*100 + "\n" +
		"2011-02-16," + Math.random()*100 + "\n" +
		"2011-02-17," + Math.random()*100 + "\n" +
		"2011-02-18," + Math.random()*100 + "\n" +
		"2011-02-19," + Math.random()*100 + "\n" +
		"2011-02-20," + Math.random()*100 + "\n" +
		"2011-02-21," + Math.random()*100 + "\n" +
		"2011-02-22," + Math.random()*100 + "\n" +
		"2011-02-23," + Math.random()*100 + "\n" +
		"2011-02-24," + Math.random()*100 + "\n" +
		"2011-02-25," + Math.random()*100 + "\n" +
		"2011-02-26," + Math.random()*100 + "\n" +
		"2011-02-27," + Math.random()*100 + "\n" +
		"2011-02-28," + Math.random()*100 + "\n";

	new Dygraph(
		document.getElementById("div_g"),
		data,
		{
			labels: ['Date','Value'],
			underlayCallback: function(canvas, area, g) {

				canvas.fillStyle = "rgba(255, 255, 102, 1.0)";

				function highlight_period(x_start, x_end) {
					var canvas_left_x = g.toDomXCoord(x_start);
					var canvas_right_x = g.toDomXCoord(x_end);
					var canvas_width = canvas_right_x - canvas_left_x;
					canvas.fillRect(canvas_left_x, area.y, canvas_width, area.h);
				}

				var min_data_x = g.getValue(0,0);
				var max_data_x = g.getValue(g.numRows()-1,0);

				// get day of week
				var d = new Date(min_data_x);
				var dow = d.getUTCDay();

				var w = min_data_x;
				// starting on Sunday is a special case
				if (dow === 0) {
					highlight_period(w,w+12*3600*1000);
				}
				// find first saturday
				while (dow != 6) {
					w += 24*3600*1000;
					d = new Date(w);
					dow = d.getUTCDay();
				}
				// shift back 1/2 day to center highlight around the point for the day
				w -= 12*3600*1000;
				while (w < max_data_x) {
					var start_x_highlight = w;
					var end_x_highlight = w + 2*24*3600*1000;
					// make sure we don't try to plot outside the graph
					if (start_x_highlight < min_data_x) {
						start_x_highlight = min_data_x;
					}
					if (end_x_highlight > max_data_x) {
						end_x_highlight = max_data_x;
					}
					highlight_period(start_x_highlight,end_x_highlight);
					// calculate start of highlight for next Saturday
					w += 7*24*3600*1000;
				}
			}
		});
}

/* End Anomaly chart js */

/* Prediction chart */
function prediction () {
    $('#mypredicts').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: 'World\'s largest cities per 2014'
        },
        subtitle: {
            text: 'Source: <a href="http://en.wikipedia.org/wiki/List_of_cities_proper_by_population">Wikipedia</a>'
        },
        xAxis: {
            type: 'category',
            labels: {
                rotation: -45,
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Population (millions)'
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            pointFormat: 'Population in 2008: <b>{point.y:.1f} millions</b>'
        },
        series: [{
            name: 'Population',
            data: [
                ['Shanghai', 23.7],
                ['Lagos', 16.1],
                ['Istanbul', 14.2],
                ['Karachi', 14.0],
                ['Mumbai', 12.5],
                ['Moscow', 12.1],
                ['São Paulo', 11.8],
                ['Beijing', 11.7],
                ['Guangzhou', 11.1],
                ['Delhi', 11.1],
                ['Shenzhen', 10.5],
                ['Seoul', 10.4],
                ['Jakarta', 10.0],
                ['Kinshasa', 9.3],
                ['Tianjin', 9.3],
                ['Tokyo', 9.0],
                ['Cairo', 8.9],
                ['Dhaka', 8.9],
                ['Mexico City', 8.9],
                ['Lima', 8.9]
            ],
            dataLabels: {
                enabled: true,
                rotation: -90,
                color: '#FFFFFF',
                align: 'right',
                format: '{point.y:.1f}', // one decimal
                y: 10, // 10 pixels down from the top
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        }]
    });
}
/* End Prediction chart */
