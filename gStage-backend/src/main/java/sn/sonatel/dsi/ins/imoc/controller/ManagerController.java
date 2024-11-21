package sn.sonatel.dsi.ins.imoc.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.dto.ValidationRequest;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.ManagerService;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/manager-internships")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @PatchMapping("/{requestId}/validate")
    public ResponseEntity<?> validateInternshipRequest(
        @PathVariable Long requestId,
        @RequestBody ValidationRequest validationRequest
    ) {
        return managerService.validateInternshipRequest(requestId, validationRequest);
    }
}

