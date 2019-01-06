package com.bator.input;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputChunk {

    private Integer hashCode;
    private String text;
    private Date utcPostDate;
    private String source;
    private BigDecimal score;
    private BigDecimal magnitude;
}
