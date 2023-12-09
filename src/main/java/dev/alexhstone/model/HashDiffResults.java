package dev.alexhstone.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class HashDiffResults {

    Set<String> leftHashesNotPresentInRight;
    Set<String> rightHashesNotPresentInLeft;
}
