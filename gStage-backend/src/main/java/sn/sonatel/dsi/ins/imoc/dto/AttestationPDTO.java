package sn.sonatel.dsi.ins.imoc.dto;

import java.time.LocalDate;
import java.util.Date;

public record AttestationPDTO(LocalDate startDate, LocalDate endDate , String email) {

}
