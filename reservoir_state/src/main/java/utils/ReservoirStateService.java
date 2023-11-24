package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReservoirStateService {
    private Map<String, Boolean> reservoirState = new ConcurrentHashMap<>();

    public void updateReservoirState(String reservoirId, boolean hasWater) {
        reservoirState.put(reservoirId, hasWater);
    }

    public boolean isWaterAvailable(String reservoirId) {
        return reservoirState.get(reservoirId);
    }
}