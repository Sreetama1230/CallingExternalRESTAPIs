package com.api.movie;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.net.URI;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootApplication
public class MainDemoApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		System.out.println( getWinnerTotalGoals("English Premier League", 2014));

		SpringApplication.run(MainDemoApplication.class, args);

	}

	public static int getWinnerTotalGoals(String competition, int year) {

		try {

			String resp1 = getResult(competition, year);
			JSONObject winnerJson = new JSONObject(resp1);
			JSONArray data5 = winnerJson.getJSONArray("data");
			String winner = data5.getJSONObject(0).getString("winner");

			int total_goals_team1 = 0;

			total_goals_team1 = cal(competition, winner, 1, year, "team1goals");

			int total_goals_team2 = 0;

			total_goals_team2 = cal(competition, winner, 2, year, "team2goals");

			return total_goals_team2 + total_goals_team1;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;

	}

	public static int cal(String competition, String team, int no, int year, String goals) throws Exception {

		int page = 1;
		int total_goals = 0;

		String team_resp = getResultwithTeam(competition, team, no, year, page);

		JSONObject root = new JSONObject(team_resp);

		int total_pages = root.getInt("total_pages");

		JSONArray data = root.getJSONArray("data");

		for (int i = 0; i < data.length(); i++) {
			JSONObject ob = data.getJSONObject(i);
			total_goals +=  ob.getInt(goals);
		}

		page++;

		while (page <= total_pages) {
			String team_resp_next_page = getResultwithTeam(competition, team, no, year, page);

			JSONObject root_next = new JSONObject(team_resp_next_page);

			JSONArray data_next = root_next.getJSONArray("data");

			for (int i = 0; i < data_next.length(); i++) {
				JSONObject ob = data_next.getJSONObject(i);
				total_goals += ob.getInt(goals);
			}

			page++;
		}

		return total_goals;
	}

	public static String getResult(String competition, int year) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		String encodedString = URLEncoder.encode(competition, StandardCharsets.UTF_8);
		String url = "https://jsonmock.hackerrank.com/api/football_competitions?name=" + encodedString + "&year="
				+ year;

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		return response.statusCode() < 400 ? response.body() : null;

	}

	public static String getResultwithTeam( String competition, String team, int no, int year, int page)
			throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		String encodedString = URLEncoder.encode(competition, StandardCharsets.UTF_8);
		String url = "https://jsonmock.hackerrank.com/api/football_matches?year=" + year
				+ "&team" + no + "=" + team +"&competition="+encodedString + "&page=" + page;

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		return response.statusCode() < 400 ? response.body() : null;

	}
}
