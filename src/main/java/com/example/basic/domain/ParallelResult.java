package com.example.basic.domain;

import com.example.basic.entity.WebbedsDaolvMatchLab;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/6/4
 */
@Data
public class ParallelResult {

    private List<WebbedsDaolvMatchLab> uniqueList;

    private List<WebbedsDaolvMatchLab> multiList;

    private Set<Integer> zeroScores;
}
