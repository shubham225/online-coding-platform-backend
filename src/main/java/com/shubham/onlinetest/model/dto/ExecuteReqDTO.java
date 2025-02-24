package com.shubham.onlinetest.model.dto;

import com.shubham.onlinetest.model.enums.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ExecuteReqDTO {
    private UUID userProblemId;
    private Language language;
}
