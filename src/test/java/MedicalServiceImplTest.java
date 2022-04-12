import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.*;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

public class MedicalServiceImplTest {

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running MessageSenderImplTest");
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("MessageSenderImplTest complete");
    }

    @BeforeEach
    public void initTest() {
        System.out.println("Test start");
    }

    @AfterEach
    public void finalizeTest() {
        System.out.println("Test complete");
    }

    @ParameterizedTest
    @MethodSource("source")
    public void checkBloodPressureTest(PatientInfo patientInfo, BloodPressure bloodPressure, String expected){
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(patientInfo.getId()))
                .thenReturn(patientInfo);

        SendAlertServiceImpl alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl sut = new MedicalServiceImpl(patientInfoFileRepository,alertService);

        sut.checkBloodPressure(patientInfo.getId(),bloodPressure);

        ArgumentCaptor<SendAlertServiceImpl> argument = ArgumentCaptor.forClass(SendAlertServiceImpl.class);
        Mockito.verify(alertService).send(String.valueOf(argument.capture()));
        Assertions.assertEquals(expected,argument.getValue());
    }

    private static Stream<Arguments> source() {
        return Stream.of(
                Arguments.of(new PatientInfo("4923b1d3-363e-4745-a3f0-128f3a5f7ee2","Иван", "Петров", LocalDate.of(1980, 11, 26),
                                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))),
                                new BloodPressure(60, 120), "Warning, patient with id: 4923b1d3-363e-4745-a3f0-128f3a5f7ee2, need help"),
                Arguments.of(new PatientInfo("6488831e-f870-4f38-a24a-f4d32c6e67c3","Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))),
                        new BloodPressure(60, 120), "Warning, patient with id: 6488831e-f870-4f38-a24a-f4d32c6e67c3, need help")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTemperature")
    public void checkTemperatureTest(PatientInfo patientInfo, BigDecimal temperature, String expected){
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(patientInfo.getId()))
                .thenReturn(patientInfo);

        SendAlertServiceImpl alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl sut = new MedicalServiceImpl(patientInfoFileRepository,alertService);

        sut.checkTemperature(patientInfo.getId(),temperature);

        ArgumentCaptor<SendAlertServiceImpl> argument = ArgumentCaptor.forClass(SendAlertServiceImpl.class);
        Mockito.verify(alertService).send(String.valueOf(argument.capture()));
        Assertions.assertEquals(expected,argument.getValue());
    }

    private static Stream<Arguments> sourceTemperature() {
        return Stream.of(
                Arguments.of(new PatientInfo("4923b1d3-363e-4745-a3f0-128f3a5f7ee2","Иван", "Петров", LocalDate.of(1980, 11, 26),
                                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80))),
                        new BigDecimal("35.0"), "Warning, patient with id: 4923b1d3-363e-4745-a3f0-128f3a5f7ee2, need help"),
                Arguments.of(new PatientInfo("6488831e-f870-4f38-a24a-f4d32c6e67c3","Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))),
                        new BigDecimal("32"), "Warning, patient with id: 6488831e-f870-4f38-a24a-f4d32c6e67c3, need help")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceNoMessage")
    public void NoMessageTest(PatientInfo patientInfo){

        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(patientInfo.getId()))
                .thenReturn(patientInfo);

        SendAlertServiceImpl alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl sut = new MedicalServiceImpl(patientInfoFileRepository,alertService);

        sut.checkBloodPressure(patientInfo.getId(),patientInfo.getHealthInfo().getBloodPressure());
        sut.checkTemperature(patientInfo.getId(),patientInfo.getHealthInfo().getNormalTemperature());
        sut.checkTemperature(patientInfo.getId(),new BigDecimal("35.0"));

        Mockito.verifyNoInteractions(alertService);
    }

    private static Stream<Arguments> sourceNoMessage() {
        return Stream.of(
                Arguments.of(new PatientInfo("4923b1d3-363e-4745-a3f0-128f3a5f7ee2","Иван", "Петров", LocalDate.of(1980, 11, 26),
                                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)))),
                Arguments.of(new PatientInfo("6488831e-f870-4f38-a24a-f4d32c6e67c3","Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))))
        );
    }


}
