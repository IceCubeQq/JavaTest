package org.example;

import org.example.chart.ChartMapper;
import org.example.model.Player;
import org.example.model.Position;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChartMapperTest {

    @Test
    void mapNationalityDataToChart_EmptyList_ReturnsEmptyDataset() {
        List<Player> players = List.of();
        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            assertNotNull(dataset);
            assertTrue(dataset instanceof DefaultPieDataset);
            List<DefaultPieDataset> constructed = mocked.constructed();
            assertEquals(1, constructed.size());
            DefaultPieDataset mockDataset = constructed.get(0);
            verify(mockDataset, never()).setValue(anyString(), any());
        }
    }

    @Test
    void mapNationalityDataToChart_SinglePlayer_ReturnsDatasetWithOneEntry() {
        List<Player> players = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3)
        );

        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            assertNotNull(dataset);
            List<DefaultPieDataset> constructed = mocked.constructed();
            assertEquals(1, constructed.size());
            DefaultPieDataset mockDataset = constructed.get(0);

            verify(mockDataset, times(1)).setValue(eq("Russia"), any());
        }
    }

    @Test
    void mapNationalityDataToChart_MultiplePlayersDifferentNationalities_CallsSetValueCorrectly() {
        List<Player> players = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3),
                new Player("John Smith", "Team USA",
                        Position.DEFENDER, "USA", "Agency2",
                        1500, 5, 1),
                new Player("Pierre Dupont", "Paris FC",
                        Position.MIDFIELD, "France", "Agence3",
                        2000, 8, 0)
        );

        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            assertNotNull(dataset);
            DefaultPieDataset mockDataset = mocked.constructed().get(0);

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Number> valueCaptor = ArgumentCaptor.forClass(Number.class);

            verify(mockDataset, times(3)).setValue(keyCaptor.capture(), valueCaptor.capture());

            List<String> capturedKeys = keyCaptor.getAllValues();
            List<Number> capturedValues = valueCaptor.getAllValues();

            assertTrue(capturedKeys.contains("Russia"));
            assertTrue(capturedKeys.contains("USA"));
            assertTrue(capturedKeys.contains("France"));

            for (Number value : capturedValues) {
                assertEquals(1.0, value.doubleValue(), 0.001);
            }
        }
    }

    @Test
    void mapNationalityDataToChart_MultiplePlayersSameNationality_CallsSetValueOnceWithCorrectCount() {
        List<Player> players = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3),
                new Player("Петров Петр", "Медведи",
                        Position.DEFENDER, "Russia", "Агентство2",
                        1200, 5, 1),
                new Player("Сидоров Алексей", "Тигры",
                        Position.MIDFIELD, "Russia", "Агентство3",
                        1500, 8, 0)
        );

        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            DefaultPieDataset mockDataset = mocked.constructed().get(0);
            verify(mockDataset, times(1)).setValue(eq("Russia"), any());
            ArgumentCaptor<Number> valueCaptor = ArgumentCaptor.forClass(Number.class);
            verify(mockDataset).setValue(eq("Russia"), valueCaptor.capture());
            assertEquals(3.0, valueCaptor.getValue().doubleValue(), 0.001);
        }
    }

    @Test
    void mapNationalityDataToChart_NullAndEmptyNationalities_CallsSetValueWithEmptyString() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1",
                        Position.FORWARD, null, "Агент1",
                        1000, 10, 3),
                new Player("Игрок2", "Команда2",
                        Position.DEFENDER, "", "Агент2",
                        1100, 5, 1),
                new Player("Игрок3", "Команда3",
                        Position.MIDFIELD, "Russia", "Агент3",
                        1200, 8, 0)
        );

        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            DefaultPieDataset mockDataset = mocked.constructed().get(0);
            verify(mockDataset, times(1)).setValue(eq(""), any());
            verify(mockDataset, times(1)).setValue(eq("Russia"), any());
            verify(mockDataset, times(2)).setValue(anyString(), any());
            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Number> valueCaptor = ArgumentCaptor.forClass(Number.class);
            verify(mockDataset, times(2)).setValue(keyCaptor.capture(), valueCaptor.capture());
            List<String> keys = keyCaptor.getAllValues();
            List<Number> values = valueCaptor.getAllValues();
            for (int i = 0; i < keys.size(); i++) {
                if ("".equals(keys.get(i))) {
                    assertEquals(2.0, values.get(i).doubleValue(), 0.001);
                } else if ("Russia".equals(keys.get(i))) {
                    assertEquals(1.0, values.get(i).doubleValue(), 0.001);
                }
            }
        }
    }

    @Test
    void mapNationalityDataToChart_CaseSensitiveNationalities_CallsSetValueForEachCase() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1",
                        Position.FORWARD, "RUSSIA", "Агент1",
                        1000, 10, 3),
                new Player("Игрок2", "Команда2",
                        Position.DEFENDER, "Russia", "Агент2",
                        1100, 5, 1),
                new Player("Игрок3", "Команда3",
                        Position.MIDFIELD, "russia", "Агент3",
                        1200, 8, 0)
        );

        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            DefaultPieDataset mockDataset = mocked.constructed().get(0);
            verify(mockDataset, times(1)).setValue(eq("RUSSIA"), any());
            verify(mockDataset, times(1)).setValue(eq("Russia"), any());
            verify(mockDataset, times(1)).setValue(eq("russia"), any());
            verify(mockDataset, times(3)).setValue(anyString(), any());
        }
    }

    @Test
    void mapNationalityDataToChart_LargeDataset_CallsSetValueCorrectNumberOfTimes() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "Russia", "Агент1", 1000, 10, 3),
                new Player("Игрок2", "Команда2", Position.DEFENDER, "Russia", "Агент2", 1100, 5, 1),
                new Player("Игрок3", "Команда3", Position.MIDFIELD, "Russia", "Агент3", 1200, 8, 0),
                new Player("Игрок4", "Команда4", Position.FORWARD, "USA", "Агент4", 1300, 12, 2),
                new Player("Игрок5", "Команда5", Position.DEFENDER, "USA", "Агент5", 1400, 6, 1)
        );

        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            DefaultPieDataset mockDataset = mocked.constructed().get(0);
            verify(mockDataset, times(2)).setValue(anyString(), any());
            verify(mockDataset, times(1)).setValue(eq("Russia"), any());
            verify(mockDataset, times(1)).setValue(eq("USA"), any());
            ArgumentCaptor<Number> valueCaptor = ArgumentCaptor.forClass(Number.class);
            verify(mockDataset, times(2)).setValue(anyString(), valueCaptor.capture());

            List<Number> values = valueCaptor.getAllValues();
            boolean hasValue3 = values.stream().anyMatch(v -> Math.abs(v.doubleValue() - 3.0) < 0.001);
            boolean hasValue2 = values.stream().anyMatch(v -> Math.abs(v.doubleValue() - 2.0) < 0.001);
            assertTrue(hasValue3, "Должно быть значение 3");
            assertTrue(hasValue2, "Должно быть значение 2");
        }
    }

    @Test
    void mapNationalityDataToChart_CreatesOnlyOneDatasetInstance() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "Russia", "Агент1", 1000, 10, 3)
        );
        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            List<DefaultPieDataset> constructed = mocked.constructed();
            assertEquals(1, constructed.size(), "Должен быть создан только один экземпляр DefaultPieDataset");
        }
    }

    @ParameterizedTest
    @MethodSource("provideNationalityDataTestCases")
    void mapNationalityDataToChart_ParameterizedTests_WithMockito(List<Player> players,
                                                                  String[] expectedCountries,
                                                                  long[] expectedCounts) {
        try (MockedConstruction<DefaultPieDataset> mocked = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            DefaultPieDataset mockDataset = mocked.constructed().get(0);
            verify(mockDataset, times(expectedCountries.length)).setValue(anyString(), any());
            for (int i = 0; i < expectedCountries.length; i++) {
                String expectedCountry = expectedCountries[i];
                long expectedCount = expectedCounts[i];
                ArgumentCaptor<Number> valueCaptor = ArgumentCaptor.forClass(Number.class);
                verify(mockDataset).setValue(eq(expectedCountry), valueCaptor.capture());
                Number capturedValue = valueCaptor.getValue();
                assertEquals((double) expectedCount, capturedValue.doubleValue(), 0.001,
                        String.format("Для страны %s ожидалось значение %d", expectedCountry, expectedCount));
            }
        }
    }

    @Test
    void mapNationalityDataToChart_DoesNotModifyOriginalList() {
        List<Player> originalPlayers = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3),
                new Player("John Smith", "Team USA",
                        Position.DEFENDER, "USA", "Agency2",
                        1500, 5, 1)
        );

        @SuppressWarnings("unchecked")
        List<Player> spyPlayers = spy(originalPlayers);
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(spyPlayers);
        verify(spyPlayers, never()).add(any());
        verify(spyPlayers, never()).remove(any());
        verify(spyPlayers, never()).clear();
        verify(spyPlayers, times(1)).stream();
    }

    private static Stream<Arguments> provideNationalityDataTestCases() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new Player("A", "T1", Position.FORWARD, "Brazil", "Ag1", 1000, 10, 0),
                                new Player("B", "T2", Position.DEFENDER, "Brazil", "Ag2", 1100, 5, 1),
                                new Player("C", "T3", Position.MIDFIELD, "Argentina", "Ag3", 1200, 8, 0),
                                new Player("D", "T4", Position.FORWARD, "Brazil", "Ag4", 1300, 12, 2)
                        ),
                        new String[]{"Brazil", "Argentina"},
                        new long[]{3L, 1L}
                ),
                Arguments.of(
                        List.of(
                                new Player("A", "T1", Position.FORWARD, "Spain", "Ag1", 1000, 10, 0),
                                new Player("B", "T2", Position.DEFENDER, "Spain", "Ag2", 1100, 5, 1),
                                new Player("C", "T3", Position.MIDFIELD, "Spain", "Ag3", 1200, 8, 0)
                        ),
                        new String[]{"Spain"},
                        new long[]{3L}
                )
        );
    }

    @Test
    void mapNationalityDataToChart_NullList_ThrowsNullPointerException() {
        List<Player> players = null;
        assertThrows(NullPointerException.class, () -> {
            ChartMapper.mapNationalityDataToChart(players);
        });
    }

    @Test
    void mapNationalityDataToChart_ReturnsPieDatasetInstance() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "Russia", "Агент1", 1000, 10, 3)
        );
        try (MockedConstruction<DefaultPieDataset> ignored = mockConstruction(DefaultPieDataset.class)) {
            PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
            assertNotNull(dataset);
            assertTrue(dataset instanceof DefaultPieDataset);
        }
    }
}
