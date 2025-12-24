package com.xmu.service.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Function:
 * Author:wdq
 * Date:2025/12/23 15:45
 */
@Getter
@Setter
@NoArgsConstructor
public class AppointmentDto {
    private LocalDateTime appointmentTime;
}