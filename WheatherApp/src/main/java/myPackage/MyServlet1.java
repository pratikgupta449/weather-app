package myPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MyServlet1
 */
public class MyServlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//API Key
		String apiKey="4def364dee13f772bda1a53cadb96a8b";

		//getting city name from html page
		String city=request.getParameter("cityName");
		System.out.println(city);
		
		// URL encode the city name(remove the space and put %20 to add the the 2 words)
	    String encodedCity = URLEncoder.encode(city, "UTF-8");
	    //System.out.println(encodedCity);

		//URL of openWeatherMap API and add city name or API key
		String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey;

		//API URL integration(means converting String apiURL as live URL)
		//Creating object of URL
		try {
			URL url=new URL(apiURL);
			HttpURLConnection connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// what ever we getting data from network we have to read it
			InputStream inputStream = connection.getInputStream();

			// reading the data that we getting from inputStream
			InputStreamReader reader= new InputStreamReader(inputStream);

			//want to store the data into string.\
			// we are using StringBuilder because dynamic data we getting(and in String we stored static string data)
			StringBuilder responseContent = new StringBuilder();

			//now we use Scanner class to scan the reader data.
			//we use Scanner sc=new Scanner(System.in); it will scan the data from the console.
			//so we do not take (System.in) instead of these we take reader to scan the data whatever we 
			//getting or stored inside reader.
			Scanner scanner= new Scanner(reader);

			//now we are using while loop and using hasNext() to read or scan the data from top to bottom 
			// whatever the data getting form scanner.
			while(scanner.hasNext()) {
				// now whatever we have getting data from responseContent and scanner we have to concatenate the data and
				// and after that we have to move into nextline to print others data.
				responseContent.append(scanner.nextLine());

			}
			scanner.close();
			//System.out.println(responseContent);  // the data we getting is in String now we have to convert into JSON format
			// to display in normal form.
			// so we do type Casting/parsing from String to JSON format.
			//Gson is a Java library developed by Google that is used to convert Java Objects 
			//into their JSON representation and vice versa.
			//Serialization: Converting Java objects to JSON format.
			Gson gson=new Gson();
			//Converts the JSON string responseContent.toString() into a JsonObject.
			JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
			//System.out.println(jsonObject);

			//Date and Time
			long dateTimeStamp = jsonObject.get("dt").getAsLong() * 1000;
			Date date= new Date(dateTimeStamp);
			//System.out.println(date);
			// now convert date object into string formate by using toString() method
			String stringDate=date.toString();
			//System.out.println(stringDate);

			//Temperature
			// converting JsonObject into double datatype
			double tempratureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			//converting kelvi to celsius.
			//System.out.println(tempratureKelvin);
			int tempratureCelsius = (int) (tempratureKelvin - 273.15);
			//System.out.println(tempratureCelsius);

			//Humidity
			// converting JsonObject into int datatype
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

			//Wind Speed
			// converting JsonObject into double datatype
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

			//Weather condition
			// converting JsonObject into String datatype
			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
			//System.out.println(weatherCondition);


			//set the data as request attributes(for sending to the JSP page)
			request.setAttribute("city",city);
			request.setAttribute("date",stringDate);
			request.setAttribute("temperature",tempratureCelsius);
			request.setAttribute("humidity",humidity);
			request.setAttribute("windSpeed",windSpeed);
			request.setAttribute("weatherCondition",weatherCondition);
			request.setAttribute("weatherData",responseContent.toString());


			//closing connection
			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//forward the all details or request to the weather.jsp page.
		request.getRequestDispatcher("index.jsp").forward(request,response);

	}

}
