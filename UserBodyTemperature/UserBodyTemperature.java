

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.math.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootApplication
public class UserBodyTemperature {

	public static void main(String[] args) throws IOException, InterruptedException {

		String result = UserBodyTemperature.getAverageTemperatureForUser(1);

		System.out.println(result);
		SpringApplication.run(UserBodyTemperature.class, args);

	}

	public static String getAverageTemperatureForUser(int userId) {

		try {

			String resp = getResultWithHttpClient(userId, 1);

			JSONObject root = new JSONObject(resp);
			int page = root.getInt("page");
			int total = root.getInt("total");
			if (total == 0) {
				return "0";
			}
			int total_pages = root.getInt("total_pages");

			JSONArray data = root.getJSONArray("data");
			double sum = 0;
			long count = 0;

			for (int i = 0; i < data.length(); i++) {
				count++;
				JSONObject object = data.getJSONObject(i);
				sum = sum + object.getJSONObject("vitals").getDouble("bodyTemperature");
			}

			page++;
			while (page <= total_pages) {

				String resp1 = getResultWithHttpClient(userId, page);

				JSONObject root1 = new JSONObject(resp1);
				JSONArray data1 = root1.getJSONArray("data");

				for (int i = 0; i < data1.length(); i++) {
					count++;
					JSONObject object1 = data1.getJSONObject(i);
					sum = sum + object1.getJSONObject("vitals").getDouble("bodyTemperature");
				}

				page++;

			}

			DecimalFormat df = new DecimalFormat("0.0");

			return df.format(sum / count);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "0";

	}

	public static String getResult(int userId, int pageId) throws Exception {

		URL url = new URL("https://jsonmock.hackerrank.com/api/medical_records?userId=" + userId + "&page=" + pageId);

		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setRequestMethod("GET");
		int status = httpURLConnection.getResponseCode();
		InputStream is = (status < 400 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder response = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			response.append(line);
		}
		String resp = response.toString();

		return resp;
	}

	public static String getResultWithHttpClient(int userId, int pageId) throws Exception {


		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(
						"https://jsonmock.hackerrank.com/api/medical_records?userId=" + userId + "&page=" + pageId))
				.GET().build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		String resp =	response.statusCode() < 400 ?response.body() : null;


		return resp;
	}
}
