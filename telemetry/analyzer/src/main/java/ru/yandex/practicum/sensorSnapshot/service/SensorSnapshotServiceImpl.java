package ru.yandex.practicum.sensorSnapshot.service;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorSnapshotServiceImpl implements SensorSnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    final String EQUALS = "EQUALS";
    final String GREATER_THAN = "GREATER_THAN";
    final String LESS_THAN = "LESS_THAN";

    @Value("${analyzer.snapshot-properties.temperatureCelsius}")
    private boolean temperatureCelsius;

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouter;

    @Override
    public void processSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        List<Scenario> scenarioList = scenarioRepository.findAllByHubId(sensorsSnapshotAvro.getHubId());
        List<ScenarioCondition> allScenariosConditionList = scenarioConditionRepository
                .findAllByScenario_IdIn(scenarioList.stream()
                        .map(Scenario::getId)
                        .toList());
        List<ScenarioAction> allScenarioActionList = scenarioActionRepository.findAllByScenario_IdIn(scenarioList
                .stream()
                .map(Scenario::getId)
                .toList());

        for (Scenario scenario : scenarioList) {
            List<ScenarioCondition> scenarioConditions = allScenariosConditionList
                    .stream()
                    .filter(scenarioCondition -> scenarioCondition.getScenario().equals(scenario))
                    .toList();

            Map<String, SensorStateAvro> sensorsStateAvro = sensorsSnapshotAvro.getSensorsState();

            boolean containsAllSensors = sensorsSnapshotAvro.getSensorsState().keySet().containsAll(scenarioConditions
                    .stream()
                    .map(scenarioCondition -> scenarioCondition.getSensor().getId())
                    .toList());

            if (!containsAllSensors) {
                continue;
            }

            Map<String, Boolean> sensorIdToBooleanConditionMap = scenarioConditions.stream()
                    .collect(Collectors.toMap(
                            scenarioCondition -> scenarioCondition.getSensor().getId(),
                            scenarioCondition -> false));

            checkAllConditions(scenarioConditions, sensorsStateAvro, sensorIdToBooleanConditionMap);

            boolean runScenario = sensorIdToBooleanConditionMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(Boolean.FALSE))
                    .findFirst().isEmpty();

            if (runScenario) {
                for (ScenarioAction scenarioAction : allScenarioActionList) {
                    if (scenarioAction.getScenario().getId().equals(scenario.getId())) {
                        hubRouter.handleDeviceAction(DeviceActionProto.newBuilder()
                                .setSensorId(scenarioAction.getSensor().getId())
                                .setType(ActionTypeProto.valueOf(scenarioAction.getAction().getType()))
                                .setValue(scenarioAction.getAction().getValue())
                                .build());
                    }
                }
            }
        }
    }

    private void checkAllConditions(List<ScenarioCondition> scenarioConditions, Map<String, SensorStateAvro> sensorsStateAvro, Map<String, Boolean> sensorIdToBooleanConditionMap) {
        for (ScenarioCondition scenarioCondition : scenarioConditions) {
            String operation = scenarioCondition.getCondition().getOperation();
            Integer valueCondition = scenarioCondition.getCondition().getValue();

            boolean resultCheckCondition = switch (ConditionTypeAvro.valueOf(scenarioCondition.getCondition().getType())) {
                case MOTION -> {
                    int motion = ((MotionSensorAvro) sensorsStateAvro
                            .get(scenarioCondition.getSensor().getId())
                            .getData()).getMotion() ? 1 : 0;

                     yield checkConditionSensors(motion, operation, valueCondition);
                }
                case LUMINOSITY -> {
                    int luminosity = ((LightSensorAvro) sensorsStateAvro
                            .get(scenarioCondition.getSensor().getId())
                            .getData()).getLuminosity();

                    yield checkConditionSensors(luminosity, operation, valueCondition);
                }
                case SWITCH -> {
                    int state = ((SwitchSensorAvro) sensorsStateAvro
                            .get(scenarioCondition.getSensor().getId())
                            .getData()).getState() ? 1 : 0;

                    yield checkConditionSensors(state, operation, valueCondition);
                }
                case TEMPERATURE -> {
                    int temperature;
                    if (temperatureCelsius) {
                     temperature = ((TemperatureSensorAvro) sensorsStateAvro
                            .get(scenarioCondition.getSensor().getId())
                            .getData()).getTemperatureC();
                    } else {
                        temperature = ((TemperatureSensorAvro) sensorsStateAvro
                                .get(scenarioCondition.getSensor().getId())
                                .getData()).getTemperatureF();
                    }

                    yield checkConditionSensors(temperature, operation, valueCondition);
                }
                case CO2LEVEL -> {
                    int co2Level = ((ClimateSensorAvro) sensorsStateAvro
                            .get(scenarioCondition.getSensor().getId())
                            .getData()).getCo2Level();

                    yield checkConditionSensors(co2Level, operation, valueCondition);
                }
                case HUMIDITY -> {
                    int humidity = ((ClimateSensorAvro) sensorsStateAvro
                            .get(scenarioCondition.getSensor().getId())
                            .getData()).getHumidity();

                    yield checkConditionSensors(humidity, operation, valueCondition);
                }
            };

            sensorIdToBooleanConditionMap.put(scenarioCondition.getSensor().getId(), resultCheckCondition);
        }
    }

    private boolean checkConditionSensors(int sensorValue, String operation, int conditionValue) {
        switch (operation) {
            case EQUALS:
                if (sensorValue == conditionValue) {
                    return true;
                }
                break;
            case GREATER_THAN:
                if (sensorValue > conditionValue) {
                    return true;
                }
                break;
            case LESS_THAN:
                if (sensorValue < conditionValue) {
                    return true;
                }
                break;
            default:
                return false;

        }
        return false;
    }

}
