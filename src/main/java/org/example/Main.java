package org.example;

import org.example.chart.ChartDrawer;
import org.example.chart.ChartMapper;
import org.example.parser.CsvParser;
import org.example.resolver.Streams;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var players = CsvParser.parseCsvToList("C:\\Users\\zhili\\IdeaProjects\\Para1711\\fakePlayers.csv");
        Streams streams = new Streams();
        streams.setPlayers(players);
//        System.out.println("Игроки без агентсва:");
//        System.out.println(streams.getCountWithoutAgency());
//        System.out.println("Максимальное количество голов, забитых защитников:");
//        System.out.println(streams.getMaxDefenderGoalsCount());
//        System.out.println("Позиция самого дорогого немецкого игрока:");
//        System.out.println(streams.getTheExpensiveGermanPlayerPosition());
//        System.out.println("Игроки по позициям:");
//        System.out.println(streams.getPlayersByPosition());
//        System.out.println("Команды:");
//        System.out.println(streams.getTeams());
//        System.out.println("Топ 5 команд по голам:");
//        System.out.println(streams.getTop5TeamsByGoalsCount());
//        System.out.println("Агентство с наименьшим количеством игроков:");
//        System.out.println(streams.getAgencyWithMinPlayersCount());
//        System.out.println("Команда с наибольшим средним значением красных карточек:");
//        System.out.println(streams.getTheRudestTeam());

        System.out.println("Вариант 4 - Доля игроков по странам:");
        var chartData = ChartMapper.mapNationalityDataToChart(players);
        ChartDrawer.showPieChart(chartData, "Доля игроков по странам");
    }
}