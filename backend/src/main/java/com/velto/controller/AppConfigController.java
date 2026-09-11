package com.velto.controller;

import com.velto.dto.AppConfigRequest;
import com.velto.dto.AppConfigResponse;
import com.velto.pattern.singleton.AppConfigSingleton;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class AppConfigController {

    @GetMapping
    public ResponseEntity<AppConfigResponse> getConfig() {
        AppConfigSingleton config = AppConfigSingleton.getInstance();
        return ResponseEntity.ok(AppConfigResponse.fromSingleton(config));
    }

    @PatchMapping
    public ResponseEntity<AppConfigResponse> updateConfig(@RequestBody AppConfigRequest request) {
        AppConfigSingleton config = AppConfigSingleton.getInstance();
        config.updateConfig(
                request.getBaseFare(),
                request.getPerKmRate(),
                request.getSurgeMultiplier(),
                request.getSharedDiscountMultiplier(),
                request.getPlatformFeePercentage(),
                request.getCurrency(),
                request.getMaintenanceMode(),
                request.getMaxSeatsPerBooking()
        );
        return ResponseEntity.ok(AppConfigResponse.fromSingleton(config));
    }

    @PostMapping("/reset")
    public ResponseEntity<AppConfigResponse> resetConfig() {
        AppConfigSingleton config = AppConfigSingleton.getInstance();
        config.resetToDefaults();
        return ResponseEntity.ok(AppConfigResponse.fromSingleton(config));
    }
}
