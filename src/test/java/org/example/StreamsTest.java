package org.example;

import org.example.model.Player;
import org.example.model.Position;
import org.example.resolver.Streams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StreamsTest {

    @Mock
    private org.example.parser.CsvParser mockParser;
    @ParameterizedTest
    @MethodSource("provideMaxDefenderGoalsCountTestData")
    void testGetMaxDefenderGoalsCount_WithMockPlayers(List<Player> mockPlayers, int expected) {
        Streams streams = new Streams(mockPlayers);
        int result = streams.getMaxDefenderGoalsCount();
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideMaxDefenderGoalsCountTestData() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), 0),
                Arguments.of(
                        List.of(createDefenderMock("Иванов Иван", 10)),
                        10
                ),
                Arguments.of(
                        List.of(createForwardMock("Иванов Иван", 10)),
                        0
                ),
                Arguments.of(
                        List.of(
                                createDefenderMock("Иванов Иван", 10),
                                createDefenderMock("Сергей Петров", 15)
                        ),
                        15
                ),
                Arguments.of(
                        List.of(createDefenderMock("Иванов Иван", 0)),
                        0
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCountWithoutAgencyTestData")
    void testGetCountWithoutAgency_WithMockPlayers(List<Player> mockPlayers, int expected) {
        Streams streams = new Streams(mockPlayers);
        int result = streams.getCountWithoutAgency();
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideCountWithoutAgencyTestData() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), 0),
                Arguments.of(
                        List.of(
                                createPlayerWithAgency("Иванов Иван", "Агентство1"),
                                createPlayerWithAgency("Сергей Петров", "Агентство2")
                        ),
                        0
                ),
                Arguments.of(
                        List.of(
                                createPlayerWithAgency("Иванов Иван", ""),
                                createPlayerWithAgency("Сергей Петров", null),
                                createPlayerWithAgency("Адам Смит", "Агентство"),
                                createPlayerWithAgency("Клинт", "")
                        ),
                        3
                )
        );
    }

    private static Stream<Arguments> provideExpensiveGermanPlayerPositionTestData() {
        return Stream.of(
                Arguments.of(List.of(), null),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "Россия", "Агентство", 1000,
                                        10, 3),
                                new Player("Адам Смит", "Пудели",
                                        Position.GOALKEEPER, "Англия", "Агентство", 2000,
                                        2, 2)
                        ),
                        null
                ),
                Arguments.of(
                        List.of(
                                new Player("Мануэль Нойер", "Бавария",
                                        Position.GOALKEEPER, "Germany", "Агентство1", 5000,
                                        0, 1)
                        ),
                        "Вратарь"
                ),
                Arguments.of(
                        List.of(
                                new Player("Игрок1", "Команда1",
                                        Position.FORWARD, "Germany", "Агентство", 3000,
                                        10, 2),
                                new Player("Игрок2", "Команда2",
                                        Position.DEFENDER, "Germany", "Агентство", 5000,
                                        5, 1),
                                new Player("Игрок3", "Команда3",
                                        Position.MIDFIELD, "Germany", "Агентство", 4000,
                                        8, 0)
                        ),
                        "Защитник"
                ),
                Arguments.of(
                        List.of(
                                new Player("Игрок1", "Команда1",
                                        Position.FORWARD, "Germany", "Агентство", 5000,
                                        10, 2),
                                new Player("Игрок2", "Команда2",
                                        Position.DEFENDER, "Germany", "Агентство", 5000,
                                        5, 1)
                        ),
                        "Нападающий"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideExpensiveGermanPlayerPositionTestData")
    void testGetTheExpensiveGermanPlayerPosition(List<Player> players, String expected) {
        var streams = new Streams(players);
        assertEquals(expected, streams.getTheExpensiveGermanPlayerPosition());
    }

    @Test
    void testGetPlayersByPosition_WithMockPlayers() {
        Player mockForward1 = mock(Player.class);
        when(mockForward1.position()).thenReturn(Position.FORWARD);
        when(mockForward1.name()).thenReturn("Иванов Иван");

        Player mockDefender = mock(Player.class);
        when(mockDefender.position()).thenReturn(Position.DEFENDER);
        when(mockDefender.name()).thenReturn("Сергей Петров");

        Player mockGoalkeeper = mock(Player.class);
        when(mockGoalkeeper.position()).thenReturn(Position.GOALKEEPER);
        when(mockGoalkeeper.name()).thenReturn("Адам Смит");

        Player mockMidfield = mock(Player.class);
        when(mockMidfield.position()).thenReturn(Position.MIDFIELD);
        when(mockMidfield.name()).thenReturn("Клинт");

        Player mockForward2 = mock(Player.class);
        when(mockForward2.position()).thenReturn(Position.FORWARD);
        when(mockForward2.name()).thenReturn("Петр Петров");

        List<Player> mockPlayers = List.of(mockForward1, mockDefender, mockGoalkeeper, mockMidfield, mockForward2);
        Streams streams = new Streams(mockPlayers);

        Map<Position, List<String>> result = streams.getPlayersByPosition();

        assertEquals(4, result.size());
        assertTrue(result.containsKey(Position.FORWARD));
        assertTrue(result.containsKey(Position.DEFENDER));
        assertTrue(result.containsKey(Position.GOALKEEPER));
        assertTrue(result.containsKey(Position.MIDFIELD));

        assertEquals(2, result.get(Position.FORWARD).size());
        assertTrue(result.get(Position.FORWARD).contains("Иванов Иван"));
        assertTrue(result.get(Position.FORWARD).contains("Петр Петров"));
        assertEquals(List.of("Сергей Петров"), result.get(Position.DEFENDER));
        assertEquals(List.of("Адам Смит"), result.get(Position.GOALKEEPER));
        assertEquals(List.of("Клинт"), result.get(Position.MIDFIELD));

        verify(mockForward1, atLeastOnce()).position();
        verify(mockForward1, atLeastOnce()).name();
        verify(mockDefender, atLeastOnce()).position();
        verify(mockDefender, atLeastOnce()).name();
        verify(mockGoalkeeper, atLeastOnce()).position();
        verify(mockGoalkeeper, atLeastOnce()).name();
        verify(mockMidfield, atLeastOnce()).position();
        verify(mockMidfield, atLeastOnce()).name();
        verify(mockForward2, atLeastOnce()).position();
        verify(mockForward2, atLeastOnce()).name();
    }

    @Test
    void testGetPlayersByPosition_EmptyList() {
        var streams = new Streams(List.of());
        Map<Position, List<String>> result = streams.getPlayersByPosition();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTeams_WithMockPlayers() {
        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.team()).thenReturn("Бульдоги");

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.team()).thenReturn("Корги");

        Player mockPlayer3 = mock(Player.class);
        when(mockPlayer3.team()).thenReturn("Бульдоги");

        List<Player> mockPlayers = List.of(mockPlayer1, mockPlayer2, mockPlayer3);
        Streams streams = new Streams(mockPlayers);

        Set<String> result = streams.getTeams();

        assertEquals(Set.of("Бульдоги", "Корги"), result);

        verify(mockPlayer1, atLeastOnce()).team();
        verify(mockPlayer2, atLeastOnce()).team();
        verify(mockPlayer3, atLeastOnce()).team();
    }




    @Test
    void testGetTop5TeamsByGoalsCount_WithMockPlayers() {
        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.team()).thenReturn("КомандаA");
        when(mockPlayer1.goals()).thenReturn(10);

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.team()).thenReturn("КомандаA");
        when(mockPlayer2.goals()).thenReturn(15);

        Player mockPlayer3 = mock(Player.class);
        when(mockPlayer3.team()).thenReturn("КомандаB");
        when(mockPlayer3.goals()).thenReturn(20);

        Player mockPlayer4 = mock(Player.class);
        when(mockPlayer4.team()).thenReturn("КомандаB");
        when(mockPlayer4.goals()).thenReturn(5);

        Player mockPlayer5 = mock(Player.class);
        when(mockPlayer5.team()).thenReturn("КомандаC");
        when(mockPlayer5.goals()).thenReturn(30);

        Player mockPlayer6 = mock(Player.class);
        when(mockPlayer6.team()).thenReturn("КомандаD");
        when(mockPlayer6.goals()).thenReturn(8);

        Player mockPlayer7 = mock(Player.class);
        when(mockPlayer7.team()).thenReturn("КомандаE");
        when(mockPlayer7.goals()).thenReturn(12);

        Player mockPlayer8 = mock(Player.class);
        when(mockPlayer8.team()).thenReturn("КомандаF");
        when(mockPlayer8.goals()).thenReturn(3);

        List<Player> mockPlayers = List.of(
                mockPlayer1, mockPlayer2, mockPlayer3, mockPlayer4,
                mockPlayer5, mockPlayer6, mockPlayer7, mockPlayer8
        );

        Streams streams = new Streams(mockPlayers);

        Map<String, Integer> result = streams.getTop5TeamsByGoalsCount();

        assertEquals(5, result.size());
        var entries = new ArrayList<>(result.entrySet());
        assertEquals("КомандаC", entries.get(0).getKey());
        assertEquals(30, entries.get(0).getValue());
        assertEquals("КомандаA", entries.get(1).getKey());
        assertEquals(25, entries.get(1).getValue());
        assertEquals("КомандаB", entries.get(2).getKey());
        assertEquals(25, entries.get(2).getValue());
        assertEquals("КомандаE", entries.get(3).getKey());
        assertEquals(12, entries.get(3).getValue());
        assertEquals("КомандаD", entries.get(4).getKey());
        assertEquals(8, entries.get(4).getValue());

        verify(mockPlayer1, atLeastOnce()).team();
        verify(mockPlayer1, atLeastOnce()).goals();
        verify(mockPlayer2, atLeastOnce()).team();
        verify(mockPlayer2, atLeastOnce()).goals();
        verify(mockPlayer3, atLeastOnce()).team();
        verify(mockPlayer3, atLeastOnce()).goals();
        verify(mockPlayer4, atLeastOnce()).team();
        verify(mockPlayer4, atLeastOnce()).goals();
        verify(mockPlayer5, atLeastOnce()).team();
        verify(mockPlayer5, atLeastOnce()).goals();
        verify(mockPlayer6, atLeastOnce()).team();
        verify(mockPlayer6, atLeastOnce()).goals();
        verify(mockPlayer7, atLeastOnce()).team();
        verify(mockPlayer7, atLeastOnce()).goals();
        verify(mockPlayer8, atLeastOnce()).team();
        verify(mockPlayer8, atLeastOnce()).goals();
    }


    @Test
    void testGetAgencyWithMinPlayersCount_WithMockPlayers() {
        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.agency()).thenReturn("Агентство1");

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.agency()).thenReturn("Агентство1");

        Player mockPlayer3 = mock(Player.class);
        when(mockPlayer3.agency()).thenReturn("Агентство2");

        Player mockPlayer4 = mock(Player.class);
        when(mockPlayer4.agency()).thenReturn("Агентство2");

        Player mockPlayer5 = mock(Player.class);
        when(mockPlayer5.agency()).thenReturn("Агентство2");

        Player mockPlayer6 = mock(Player.class);
        when(mockPlayer6.agency()).thenReturn("Агентство3");

        Player mockPlayer7 = mock(Player.class);
        when(mockPlayer7.agency()).thenReturn("Агентство4");

        List<Player> mockPlayers = List.of(
                mockPlayer1, mockPlayer2, mockPlayer3, mockPlayer4,
                mockPlayer5, mockPlayer6, mockPlayer7
        );

        Streams streams = new Streams(mockPlayers);

        String result = streams.getAgencyWithMinPlayersCount();
        assertEquals("Агентство3", result);
        verify(mockPlayer1, atLeastOnce()).agency();
        verify(mockPlayer2, atLeastOnce()).agency();
        verify(mockPlayer3, atLeastOnce()).agency();
        verify(mockPlayer4, atLeastOnce()).agency();
        verify(mockPlayer5, atLeastOnce()).agency();
        verify(mockPlayer6, atLeastOnce()).agency();
        verify(mockPlayer7, atLeastOnce()).agency();
    }


    @Test
    void testGetAgencyWithMinPlayersCount_EmptyList() {
        var streams = new Streams(List.of());
        String result = streams.getAgencyWithMinPlayersCount();
        assertNull(result);
    }

    @Test
    void testGetTheRudestTeam_WithMockPlayers() {
        // Arrange
        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.team()).thenReturn("КомандаA");
        when(mockPlayer1.redCards()).thenReturn(2);

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.team()).thenReturn("КомандаA");
        when(mockPlayer2.redCards()).thenReturn(1);

        Player mockPlayer3 = mock(Player.class);
        when(mockPlayer3.team()).thenReturn("КомандаB");
        when(mockPlayer3.redCards()).thenReturn(3);

        Player mockPlayer4 = mock(Player.class);
        when(mockPlayer4.team()).thenReturn("КомандаB");
        when(mockPlayer4.redCards()).thenReturn(1);

        Player mockPlayer5 = mock(Player.class);
        when(mockPlayer5.team()).thenReturn("КомандаC");
        when(mockPlayer5.redCards()).thenReturn(0);

        List<Player> mockPlayers = List.of(
                mockPlayer1, mockPlayer2, mockPlayer3, mockPlayer4, mockPlayer5
        );

        Streams streams = new Streams(mockPlayers);

        String result = streams.getTheRudestTeam();
        assertEquals("КомандаB", result);
        verify(mockPlayer1, atLeastOnce()).team();
        verify(mockPlayer1, atLeastOnce()).redCards();
        verify(mockPlayer2, atLeastOnce()).team();
        verify(mockPlayer2, atLeastOnce()).redCards();
        verify(mockPlayer3, atLeastOnce()).team();
        verify(mockPlayer3, atLeastOnce()).redCards();
        verify(mockPlayer4, atLeastOnce()).team();
        verify(mockPlayer4, atLeastOnce()).redCards();
        verify(mockPlayer5, atLeastOnce()).team();
        verify(mockPlayer5, atLeastOnce()).redCards();
    }


    @Test
    void testConstructorWithMockParser() {
        var streams = new Streams(mockParser);
        assertNotNull(streams);
        assertNotNull(streams.getPlayers());
        assertTrue(streams.getPlayers().isEmpty());
    }

    @Test
    void testSetPlayers() {
        var streams = new Streams();
        Player mockPlayer = mock(Player.class);
        List<Player> mockPlayers = List.of(mockPlayer);
        streams.setPlayers(mockPlayers);
        assertEquals(mockPlayers, streams.getPlayers());
    }


    @Test
    void testAllMethodsWithNullPlayers() {
        var streams = new Streams();
        try {
            Field playersField = Streams.class.getDeclaredField("players");
            playersField.setAccessible(true);
            playersField.set(streams, null);
        } catch (Exception e) {
            fail("Не удалось");
        }

        assertNull(streams.getPlayers());
        assertEquals(0, streams.getCountWithoutAgency());
        assertEquals(0, streams.getMaxDefenderGoalsCount());
        assertNull(streams.getTheExpensiveGermanPlayerPosition());
        assertTrue(streams.getPlayersByPosition().isEmpty());
        assertTrue(streams.getTeams().isEmpty());
        assertTrue(streams.getTop5TeamsByGoalsCount().isEmpty());
        assertNull(streams.getAgencyWithMinPlayersCount());
        assertEquals("", streams.getTheRudestTeam());
    }

    @Test
    void testGetAgencyWithMinPlayersCount_AllNullAndEmptyAgencies() {
        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.agency()).thenReturn(null);

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.agency()).thenReturn("");

        Player mockPlayer3 = mock(Player.class);
        when(mockPlayer3.agency()).thenReturn(null);

        Player mockPlayer4 = mock(Player.class);
        when(mockPlayer4.agency()).thenReturn("");

        List<Player> mockPlayers = List.of(mockPlayer1, mockPlayer2, mockPlayer3, mockPlayer4);

        Streams streams = new Streams(mockPlayers);

        String result = streams.getAgencyWithMinPlayersCount();
        assertNull(result);
        verify(mockPlayer1, atLeastOnce()).agency();
        verify(mockPlayer2, atLeastOnce()).agency();
        verify(mockPlayer3, atLeastOnce()).agency();
        verify(mockPlayer4, atLeastOnce()).agency();
    }


    @Test
    void testGetTheExpensiveGermanPlayerPosition_WithZeroTransferCost() {
        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.nationality()).thenReturn("Germany");
        when(mockPlayer1.transferCost()).thenReturn(0);

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.nationality()).thenReturn("Germany");
        when(mockPlayer2.transferCost()).thenReturn(0);

        Player mockPlayer3 = mock(Player.class);
        when(mockPlayer3.nationality()).thenReturn("Germany");
        when(mockPlayer3.position()).thenReturn(Position.MIDFIELD);
        when(mockPlayer3.transferCost()).thenReturn(1000);

        List<Player> mockPlayers = List.of(mockPlayer1, mockPlayer2, mockPlayer3);

        Streams streams = new Streams(mockPlayers);
        String result = streams.getTheExpensiveGermanPlayerPosition();
        assertEquals("Полузащитник", result);
        verify(mockPlayer1, atLeastOnce()).nationality();
        verify(mockPlayer1, atLeastOnce()).transferCost();

        verify(mockPlayer2, atLeastOnce()).nationality();
        verify(mockPlayer2, atLeastOnce()).transferCost();

        verify(mockPlayer3, atLeastOnce()).nationality();
        verify(mockPlayer3, atLeastOnce()).transferCost();
        verify(mockPlayer3, atLeastOnce()).position();
    }

    private static Player createDefenderMock(String name, int goals) {
        Player mock = mock(Player.class);
        when(mock.position()).thenReturn(Position.DEFENDER);
        when(mock.goals()).thenReturn(goals);
        return mock;
    }

    private static Player createForwardMock(String name, int goals) {
        Player mock = mock(Player.class);
        when(mock.position()).thenReturn(Position.FORWARD);
        when(mock.goals()).thenReturn(goals);
        return mock;
    }

    private static Player createPlayerWithAgency(String name, String agency) {
        Player mock = mock(Player.class);
        when(mock.agency()).thenReturn(agency);
        return mock;
    }

    private static Player createGermanPlayerMock(String name, Position position, int transferCost) {
        Player mock = mock(Player.class);
        when(mock.nationality()).thenReturn("Germany");
        when(mock.position()).thenReturn(position);
        when(mock.transferCost()).thenReturn(transferCost);
        return mock;
    }
}
