package ru.yandex.practicum.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ru.yandex.practicum.model.id.ScenarioConditionId;

import java.util.Objects;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ScenarioCondition {

    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id", nullable = false)
    private Condition condition;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ScenarioCondition that = (ScenarioCondition) o;
        return getScenario() != null && Objects.equals(getScenario(), that.getScenario());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}