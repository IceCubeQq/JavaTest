package org.example.resolver;

import org.example.model.Player;
import org.example.model.Position;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IResolver {

    List<Player> getPlayers();

    // Выведите количество игроков, интересы которых не представляет агентство.
    int getCountWithoutAgency();

    // Выведите максимальное число голов, забитых защитником.
    int getMaxDefenderGoalsCount();

    // Выведите русское название позиции самого дорогого немецкого игрока.
    String getTheExpensiveGermanPlayerPosition();

    // Верните имена игроков, сгруппированных по позициям, на которых они играют.
    Map<Position, List<String>> getPlayersByPosition();

    // Верните множество команд, которые представлены в чемпионате.
    Set<String> getTeams();

    // Верните топ-5 команд по количеству забитых мячей, и количество этих мячей.
    Map<String, Integer> getTop5TeamsByGoalsCount();

    // Верните агентство, сумма игроков которого наименьшая. 
    String getAgencyWithMinPlayersCount();

    // Выберите команду с наибольшим средним числом удалений на одного игрока.
    String getTheRudestTeam();
}