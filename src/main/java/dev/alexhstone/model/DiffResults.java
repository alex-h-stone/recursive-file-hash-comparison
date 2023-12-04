package dev.alexhstone.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DiffResults {

    List<FileHashResult>  leftFilesNotPresentInRight;
    List<FileHashResult>  rightFilesNotPresentInLeft;
}
