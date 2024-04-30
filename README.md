## Stub vs Mock

### Stub

A stub is an object with a hardcoded response that represents a specific state of a real object:

```java
public class WeatherStub implements WeatherClient {
    @Override
    public String getTodayWeather() {
        return "{\"temperature\": 20, \"condition\": \"Sunny\"}";
    }
}
```

### Mock

A mock, verifies if a method was called and tests behavior:

```java
import org.mockito.Mockito.*;

public class WeatherMock {
    WeatherClient mockWeatherClient = Mockito.mock(WeatherClient.class);

    public static void main(String[] args) {
        when(mockWeatherClient.getTodayWeather()).thenReturn("{\"temperature\": 20, \"condition\": \"Sunny\"}");
        
        verify(mockWeatherClient, times(1)).getTodayWeather();
    }
}
```

