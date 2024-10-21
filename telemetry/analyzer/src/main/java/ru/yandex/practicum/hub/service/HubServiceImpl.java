package ru.yandex.practicum.hub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.model.id.ScenarioActionId;
import ru.yandex.practicum.model.id.ScenarioConditionId;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {
    private final ConversionService conversionService;
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Override
    public void pocessDeviceEvent(HubEventProto hubEventProto) {
        if (Objects.isNull(hubEventProto)) {
            return;
        }

        switch (hubEventProto.getPayloadCase()) {
            case DEVICE_ADDED:
                if (sensorRepository.existsByIdAndHubId(hubEventProto.getDeviceAdded().getId(),
                        hubEventProto.getHubId())) {
                    return;
                }
                Sensor sensor = conversionService.convert(hubEventProto, Sensor.class);
                sensorRepository.save(Objects.requireNonNull(sensor));
                break;
            case DEVICE_REMOVED:
                sensorRepository.deleteById(hubEventProto.getDeviceRemoved().getId());
                break;
            case SCENARIO_ADDED:
                addScenario(hubEventProto);
                break;
            case SCENARIO_REMOVED:
                removeScenario(hubEventProto);
                break;
            default:
                break;
        }
    }

    private void addScenario(HubEventProto hubEventProto) {
        Scenario scenario = Scenario.builder()
                .hubId(hubEventProto.getHubId())
                .name(hubEventProto.getScenarioAdded().getName())
                .build();

        scenarioRepository.save(scenario);

        List<Condition> conditions = new ArrayList<>();
        List<ScenarioCondition> scenarioConditions = new ArrayList<>();

        hubEventProto.getScenarioAdded().getConditionList().forEach(conditionStream -> {
            Condition condition = conversionService.convert(conditionStream, Condition.class);
            assert condition != null;
            conditions.add(condition);
            ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                    .id(new ScenarioConditionId())
                    .scenario(scenario)
                    .sensor(sensorRepository.findById(conditionStream.getSensorId()).orElseThrow())
                    .condition(condition)
                    .build();
            scenarioConditions.add(scenarioCondition);
        });

        conditionRepository.saveAll(conditions);
        scenarioConditionRepository.saveAll(scenarioConditions);

        List<Action> actions = new ArrayList<>();
        List<ScenarioAction> scenarioActions = new ArrayList<>();

        hubEventProto.getScenarioAdded().getActionList().forEach(actionProto -> {
            Action action = conversionService.convert(actionProto, Action.class);
            assert action != null;
            actions.add(action);
            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(new ScenarioActionId())
                    .action(action)
                    .sensor(sensorRepository.findById(actionProto.getSensorId()).orElseThrow())
                    .scenario(scenario)
                    .build();
            scenarioActions.add(scenarioAction);
        });

        actionRepository.saveAll(actions);
        scenarioActionRepository.saveAll(scenarioActions);
    }

    private void removeScenario(HubEventProto hubEventProto) {
        Scenario scenario = scenarioRepository
                .findByHubIdAndName(hubEventProto.getHubId(), hubEventProto.getScenarioRemoved().getName())
                .orElseThrow();

        scenarioRepository.delete(scenario);
    }

}
